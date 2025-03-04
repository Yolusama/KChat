package KChat.DbOption.ServiceImpl;

import KChat.Common.CachingKeys;
import KChat.DbOption.Mapper.ChatMessageMapper;
import KChat.DbOption.Mapper.HeadMessageMapper;
import KChat.DbOption.Service.IChatMessageService;
import KChat.Entity.HeadMessage;
import KChat.Entity.VO.HeadMessageVO;
import KChat.Model.ArrayDataModel;
import KChat.Model.HeadMessageModel;
import KChat.Service.RedisCache;
import KChat.Utils.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ChatMessageService implements IChatMessageService {
    private final ChatMessageMapper messageMapper;
    private final HeadMessageMapper headMapper;

    @Autowired
    public ChatMessageService(ChatMessageMapper messageMapper,HeadMessageMapper headMapper){
        this.messageMapper = messageMapper;
        this.headMapper = headMapper;
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
    public List<HeadMessageVO> getHeadMessages(String userId, RedisCache redis) {
        ArrayDataModel<HeadMessageVO> model;
        String key = String.format("Caching_%s_%s",userId, CachingKeys.GetHeadMessages);
        if (redis.has(key)) {
            model = (ArrayDataModel<HeadMessageVO>) redis.get(key);
        }
        else {
            List<HeadMessageVO> messages = headMapper.getHeadMessages(userId);
            List<Integer> counts = messageMapper.getUnReadCounts(userId);
            for (int i = 0; i < counts.size(); i++)
                messages.get(i).setUnReadCount(counts.get(i));
            model = new ArrayDataModel<>();
            model.setData(messages);
        }
        return model.getData();
    }
}
