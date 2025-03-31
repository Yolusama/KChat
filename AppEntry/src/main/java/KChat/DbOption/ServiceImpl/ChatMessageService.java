package KChat.DbOption.ServiceImpl;

import KChat.Common.Constants;
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
import KChat.Model.MessageRecordModel;
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
    private final UserMapper userMapper;

    @Autowired
    public ChatMessageService(ChatMessageMapper messageMapper,HeadMessageMapper headMapper,
                              MessageRecordMapper recordMapper,
                              GroupMessageRecordMapper groupMessageRecordMapper,
                              UserGroupMapper groupMapper,
                              UserMapper userMapper){
        this.messageMapper = messageMapper;
        this.headMapper = headMapper;
        this.recordMapper = recordMapper;
        this.groupMessageRecordMapper = groupMessageRecordMapper;
        this.groupMapper = groupMapper;
        this.userMapper = userMapper;
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
        boolean isGroup = model.getContactId().indexOf(Constants.GroupIdPrefix)>=Constants.None;
        if(isGroup) {
            List<String> onlineMemberIds = groupMapper.getMemberIds(model.getContactId(),false);
            for(String id:onlineMemberIds)
            {
                MessageRecord record = new MessageRecord();
                record.setContactId(id);
                msgProducer.produceAndSend(record);
            }
        }
        else {
            Boolean online = userMapper.isOnline(model.getContactId());
            if (!online) {
                LambdaUpdateWrapper<HeadMessage> wrapper = new LambdaUpdateWrapper<>();
                wrapper.eq(HeadMessage::getUserId, model.getContactId()).eq(HeadMessage::getContactId, model.getUserId())
                        .set(HeadMessage::getContent, model.getContent()).set(HeadMessage::getTime, model.getTime());
                headMapper.update(wrapper);
            }
            MessageRecord record = new MessageRecord();
            record.setContactId(model.getContactId());
            msgProducer.produceAndSend(record);
        }
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
        if(model.getContactId().indexOf(Constants.GroupIdPrefix)>=Constants.None){
            List<GroupMessageRecord> records = new ArrayList<>();
            for(String userId:groupMapper.getMemberIds(model.getContactId(),null))
            {
                GroupMessageRecord groupMessageRecord = new GroupMessageRecord();
                groupMessageRecord.setMessageId(message.getId());
                groupMessageRecord.setUserId(model.getUserId());
                groupMessageRecord.setMemberId(userId);
                groupMessageRecord.setGroupId(model.getContactId());
                groupMessageRecord.setFilePath(model.getFilePath());
                if(!model.getType().equals(MessageType.COMMON.value())
                        &&!model.getType().equals(MessageType.PICTURE.value()))
                    groupMessageRecord.setDownloaded(userId.equals(model.getUserId()));
                groupMessageRecord.setSelfRecord(userId.equals(model.getUserId()));
                records.add(groupMessageRecord);
            }
            if(records.size()>0)
                groupMessageRecordMapper.batchInsert(records);
            return;
        }
        MessageRecord record = new MessageRecord();
        record.setMessageId(message.getId());
        record.setUserId(model.getUserId());
        record.setContactId(model.getContactId());
        record.setUserSent(true);
        record.setFilePath(model.getFilePath());
        record.setDownloaded(true);
        MessageRecord contactRecord = new MessageRecord();
        contactRecord.setMessageId(message.getId());
        contactRecord.setUserId(model.getUserId());
        contactRecord.setContactId(model.getContactId());
        contactRecord.setUserSent(false);
        contactRecord.setFilePath(null);
        contactRecord.setDownloaded(false);
        recordMapper.batchInsert(List.of(record,contactRecord));
    }

    @Override
    public String uploadFile(String suffix, MultipartFile file, FileService fileService) {
        return fileService.uploadCacheFile(file,suffix);
    }

    @Override
    @Transactional
    public void updateFilePath(MessageRecordModel model, MQMsgProducer msgProducer) {
        int res;
        if(model.getContactId().indexOf(Constants.GroupIdPrefix)<Constants.None)
        res = recordMapper.updateFilePath(model.getMessageId(),model.getUserId()
                ,model.getContactId(),model.getFilePath());
        else
            res = groupMessageRecordMapper.updateFilePath(model.getRecordId(),model.getFilePath());
        if(res!=Constants.None)
        {
            MessageRecord record = new MessageRecord();
            record.setContactId(model.getUserId());
            msgProducer.produceAndSend(record);
        }
    }
}