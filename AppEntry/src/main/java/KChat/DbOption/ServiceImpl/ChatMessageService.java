package KChat.DbOption.ServiceImpl;

import KChat.DbOption.Mapper.ChatMessageMapper;
import KChat.DbOption.Mapper.HeadMessageMapper;
import KChat.DbOption.Mapper.UserApplyMapper;
import KChat.DbOption.Service.IChatMessageService;
import KChat.Entity.ChatMessage;
import KChat.Entity.HeadMessage;
import KChat.Entity.VO.ChatMessageVO;
import KChat.Entity.VO.HeadMessageVO;
import KChat.Entity.VO.PagedData;
import KChat.Model.ChatMessageModel;
import KChat.Model.HeadMessageModel;
import KChat.Service.MQMsgProducer;
import KChat.Utils.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ChatMessageService implements IChatMessageService {
    private final ChatMessageMapper messageMapper;
    private final HeadMessageMapper headMapper;
    private final UserApplyMapper applyMapper;

    @Autowired
    public ChatMessageService(ChatMessageMapper messageMapper,HeadMessageMapper headMapper,UserApplyMapper applyMapper){
        this.messageMapper = messageMapper;
        this.headMapper = headMapper;
        this.applyMapper = applyMapper;
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
        List<Integer> counts = messageMapper.getUnReadCounts(userId);
        for (int i = 0; i < messages.size(); i++)
            messages.get(i).setUnReadCount(counts.get(i));
        return messages;
    }

    @Override
    public PagedData<ChatMessageVO> getChatMessages(Integer page, Integer pageSize, String userId, String contactId) {
        Page<ChatMessageVO> pageRes = Page.of(page,pageSize);
        var data = messageMapper.getChatMessages(pageRes,userId,contactId);
        return new PagedData<>(data, pageRes.getTotal());
    }

    @Override
    @Transactional
    public Long createMessage(ChatMessageModel model, MQMsgProducer msgProducer) {
        ChatMessage message = new ChatMessage();
        ObjectUtil.copy(model,message);
        message.setRead(false);
        messageMapper.insert(message);
        msgProducer.produceAndSend(message);
        return message.getId();
    }

    @Override
    @Transactional
    public void createOfflineMessage(ChatMessageModel model) {
        ChatMessage message = new ChatMessage();
        ObjectUtil.copy(model,message);
        message.setRead(false);
        messageMapper.insert(message);
        LambdaQueryWrapper<HeadMessage> wrapper1 = new LambdaQueryWrapper<>();
        wrapper1.eq(HeadMessage::getUserId,model.getUserId()).eq(HeadMessage::getContactId,message.getContactId());
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
        wrapper2.eq(HeadMessage::getUserId,model.getContactId()).eq(HeadMessage::getContactId,message.getUserId());
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
}
