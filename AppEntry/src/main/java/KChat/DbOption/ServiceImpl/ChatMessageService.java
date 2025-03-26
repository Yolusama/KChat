package KChat.DbOption.ServiceImpl;

import KChat.Common.Constants;
import KChat.Common.Pair;
import KChat.DbOption.Mapper.*;
import KChat.DbOption.Service.IChatMessageService;
import KChat.Entity.ChatMessage;
import KChat.Entity.Enum.MessageType;
import KChat.Entity.GroupMessageRecord;
import KChat.Entity.HeadMessage;
import KChat.Entity.MessageRecord;
import KChat.Entity.VO.ChatMessageVO;
import KChat.Entity.VO.HeadMessageVO;
import KChat.Entity.VO.PagedData;
import KChat.Model.ChatMessageModel;
import KChat.Model.HeadMessageModel;
import KChat.Service.FileService;
import KChat.Service.MQMsgProducer;
import KChat.Utils.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Service
public class ChatMessageService implements IChatMessageService {
    private final ChatMessageMapper messageMapper;
    private final HeadMessageMapper headMapper;
    private final MessageRecordMapper recordMapper;
    private final GroupMessageRecordMapper groupMessageRecordMapper;
    private final UserGroupMapper groupMapper;

    @Autowired
    public ChatMessageService(ChatMessageMapper messageMapper,HeadMessageMapper headMapper,
                              MessageRecordMapper recordMapper,
                              GroupMessageRecordMapper groupMessageRecordMapper,
                              UserGroupMapper groupMapper){
        this.messageMapper = messageMapper;
        this.headMapper = headMapper;
        this.recordMapper = recordMapper;
        this.groupMessageRecordMapper = groupMessageRecordMapper;
        this.groupMapper = groupMapper;
    }

    @Override
    @Transactional
    public Long createHeadMessage(HeadMessageModel model) {
        HeadMessage headMessage = new HeadMessage();
        ObjectUtil.copy(model,headMessage);
        headMapper.insert(headMessage);
        return headMessage.getId();
    }

    @Override
    @Transactional
    public Long freshHeadMessage(HeadMessageModel model) {
        if(model.getContactId().indexOf(Constants.GroupIdPrefix)>=0)
        {
            long res = Constants.None;
            ReentrantLock lock = new ReentrantLock();
            LambdaUpdateWrapper<HeadMessage> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(HeadMessage::getContactId,model.getContactId())
                    .set(HeadMessage::getContent,model.getContent())
                    .set(HeadMessage::getTime,model.getTime());
            headMapper.update(wrapper);
            lock.lock();
            List<String> memberIds = groupMapper.getMemberIds(model.getContactId(),null);
            List<HeadMessage> toInsert = new ArrayList<>();
            for(var memberId:memberIds){
                LambdaQueryWrapper<HeadMessage> wrapper1 = new LambdaQueryWrapper<>();
                wrapper1.eq(HeadMessage::getUserId,memberId).eq(HeadMessage::getContactId,model.getContactId());
                HeadMessage headMessage = headMapper.selectOne(wrapper1);
                if(headMessage == null){
                   headMessage = new HeadMessage();
                   headMessage.setUserId(memberId);
                   headMessage.setContent(model.getContent());
                   headMessage.setContactId(model.getContactId());
                   headMessage.setTime(model.getTime());
                   if(memberId.equals(model.getUserId())){
                       headMapper.insert(headMessage);
                       res = headMessage.getId();
                   }
                   else
                       toInsert.add(headMessage);
                }
                else{
                    if(headMessage.getUserId().equals(model.getUserId()))
                        res = headMessage.getId();
                }
            }
            if(toInsert.size()>Constants.None)
                headMapper.batchInsert(toInsert);
            lock.unlock();
            return res;
        }
        HeadMessage headMessage = headMapper.selectOne(new LambdaQueryWrapper<HeadMessage>().
                eq(HeadMessage::getUserId,model.getUserId()).eq(HeadMessage::getContactId,model.getContactId()));
        if(headMessage!=null){
            headMessage.setTime(model.getTime());
            headMessage.setContent(model.getContent());
            headMapper.updateById(headMessage);
        }
        else{
            headMessage = new HeadMessage();
            ObjectUtil.copy(model,headMessage);
            headMapper.insert(headMessage);
        }
        return headMessage.getId();
    }

    @Override
    public List<HeadMessageVO> getHeadMessages(String userId) {
        List<HeadMessageVO> messages = headMapper.getHeadMessages(userId);
        var counts = messageMapper.getUnReadCounts(userId);
        for(var countVO:counts){
            Optional<HeadMessageVO> optional = messages.stream().filter(c->c.getId().equals(countVO.getHeadMessageId()))
                    .findFirst();
           if(!optional.isEmpty())
               optional.get().setUnReadCount(countVO.getUnReadCount());
        }
        return messages.stream().sorted((h1,h2)->(int)(h2.getTime().getTime()-h1.getTime().getTime()))
                .collect(Collectors.toList());
    }

    @Override
    public PagedData<ChatMessageVO> getChatMessages(Integer page, Integer pageSize, String userId, String contactId,
                                                    FileService fileService) {
        Page<ChatMessageVO> pageRes = Page.of(page,pageSize);
        List<ChatMessageVO> data;
        if(contactId.indexOf(Constants.GroupIdPrefix)>=Constants.None)
            data = messageMapper.getGroupMessages(pageRes,userId,contactId);
        else
            data = messageMapper.getChatMessages(pageRes,userId,contactId);
        for(var msg:data.stream().filter(m->!m.getType().equals(MessageType.COMMON.value())).collect(Collectors.toList())){
            if(msg.fileTimeOut()){
                fileService.removeCacheFile(msg.getFileName());
            }
        }
        return new PagedData<>(data,pageRes.getTotal());
    }

    @Override
    @Transactional
    public Long createMessage(ChatMessageModel model, MQMsgProducer msgProducer) {
        ChatMessage message = new ChatMessage();
        createMessage(model,message);
        MessageRecord record = new MessageRecord();
        record.setContactId(model.getContactId());
        msgProducer.produceAndSend(record);
        return message.getId();
    }

    @Override
    @Transactional
    public void createOfflineMessage(ChatMessageModel model) {
        ChatMessage message = new ChatMessage();
        createMessage(model,message);
        LambdaQueryWrapper<HeadMessage> wrapper1 = new LambdaQueryWrapper<>();
        wrapper1.eq(HeadMessage::getUserId,model.getUserId()).eq(HeadMessage::getContactId,model.getContactId());
        HeadMessage userHeadMsg = headMapper.selectOne(wrapper1);
        if(userHeadMsg==null){
            userHeadMsg = new HeadMessage();
            userHeadMsg.setUserId(model.getUserId());
            userHeadMsg.setContactId(model.getContactId());
            userHeadMsg.setTime(model.getTime());
            userHeadMsg.setContent(model.getContent());
            headMapper.insert(userHeadMsg);
        }
        else{
            userHeadMsg.setContent(model.getContent());
            userHeadMsg.setTime(model.getTime());
            headMapper.updateById(userHeadMsg);
        }
        LambdaQueryWrapper<HeadMessage> wrapper2 = new LambdaQueryWrapper<>();
        wrapper2.eq(HeadMessage::getUserId,model.getContactId()).eq(HeadMessage::getContactId,model.getUserId());
        HeadMessage contactHeadMsg = headMapper.selectOne(wrapper2);
        if(contactHeadMsg == null){
            contactHeadMsg = new HeadMessage();
            contactHeadMsg.setUserId(model.getContactId());
            contactHeadMsg.setContactId(model.getUserId());
            contactHeadMsg.setTime(model.getTime());
            contactHeadMsg.setContent(model.getContent());
            headMapper.insert(contactHeadMsg);
        }
        else{
            contactHeadMsg.setContent(model.getContent());
            contactHeadMsg.setTime(model.getTime());
            headMapper.updateById(contactHeadMsg);
        }
    }

    private void createMessage(ChatMessageModel model, ChatMessage message){
        ObjectUtil.copy(model,message);
        message.setHandled(false);
        messageMapper.insert(message);
        MessageRecord record = new MessageRecord();
        record.setMessageId(message.getId());
        record.setUserId(model.getUserId());
        record.setContactId(model.getContactId());
        record.setUserSent(true);
        if(model.getContactId().indexOf(Constants.GroupIdPrefix)>=Constants.None){
            record.setUserSent(true);
            recordMapper.insert(record);
            List<GroupMessageRecord> records = new ArrayList<>();
            for(String userId:groupMapper.getMemberIds(model.getContactId(),null))
            {
                GroupMessageRecord groupMessageRecord = new GroupMessageRecord();
                groupMessageRecord.setRecordId(record.getId());
                groupMessageRecord.setMemberId(userId);
                records.add(groupMessageRecord);
            }
            groupMessageRecordMapper.batchInsert(records);
            return;
        }
        MessageRecord contactRecord = new MessageRecord();
        contactRecord.setMessageId(message.getId());
        contactRecord.setUserId(model.getUserId());
        contactRecord.setContactId(model.getContactId());
        contactRecord.setUserSent(false);
        recordMapper.batchInsert(List.of(record,contactRecord));
    }

    @Override
    public Pair<String, String> uploadFile(String suffix, MultipartFile file, FileService fileService) {
        return Pair.makePair(fileService.uploadCacheFile(file,suffix),file.getOriginalFilename());
    }
}

/*
     if(isGroupMsg){
            Long res = null;
            List<HeadMessage> toInsert = new ArrayList<>();
            List<String> toUpdateUserIds = new ArrayList<>();
            List<String> memberIds = groupMapper.getMemberIds(model.getContactId());
            for(String userId:memberIds){
                LambdaQueryWrapper<HeadMessage> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(HeadMessage::getUserId,userId).eq(HeadMessage::getContactId,model.getContactId());
                HeadMessage message = headMapper.selectOne(wrapper);
                if(message == null){
                    message = new HeadMessage();
                    message.setTime(model.getTime());
                    message.setUserId(model.getUserId());
                    message.setContactId(model.getContactId());
                    message.setContent(model.getContent());
                    if(message.getUserId().equals(model.getUserId()))
                    {
                        headMapper.insert(message);
                        res = message.getId();
                    }
                    else toInsert.add(message);
                }
                else{
                    toUpdateUserIds.add(message.getUserId());
                    if(message.getUserId().equals(model.getUserId()))
                        res = message.getId();
                }
            }
            if(toInsert.size()>0)
                headMapper.batchInsert(toInsert);
            if(toUpdateUserIds.size()>0)
            {
                LambdaUpdateWrapper<HeadMessage> wrapper = new LambdaUpdateWrapper<>();
                wrapper.set(HeadMessage::getTime,model.getTime()).set(HeadMessage::getContent,model.getContent())
                        .in(HeadMessage::getUserId,toUpdateUserIds).eq(HeadMessage::getContactId,model.getContactId());
                headMapper.update(wrapper);
            }
            return res;
        }
 */