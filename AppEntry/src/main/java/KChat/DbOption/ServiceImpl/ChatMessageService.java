package KChat.DbOption.ServiceImpl;

import KChat.Common.CachingKeys;
import KChat.Common.Constants;
import KChat.DbOption.Mapper.ChatMessageMapper;
import KChat.DbOption.Mapper.HeadMessageMapper;
import KChat.DbOption.Mapper.UserApplyMapper;
import KChat.DbOption.Service.IChatMessageService;
import KChat.Entity.ChatMessage;
import KChat.Entity.Enum.UserApplyStatus;
import KChat.Entity.HeadMessage;
import KChat.Entity.UserApply;
import KChat.Entity.VO.ChatMessageVO;
import KChat.Entity.VO.HeadMessageVO;
import KChat.Entity.VO.PagedData;
import KChat.Model.ArrayDataModel;
import KChat.Model.ChatMessageModel;
import KChat.Model.HeadMessageModel;
import KChat.Model.UserApplyModel;
import KChat.Utils.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
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
    public Long createMessage(ChatMessageModel model) {
        ChatMessage message = new ChatMessage();
        ObjectUtil.copy(model,message);
        messageMapper.insert(message);
        return message.getId();
    }
}
