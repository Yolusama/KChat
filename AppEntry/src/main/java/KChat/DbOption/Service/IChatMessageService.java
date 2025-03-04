package KChat.DbOption.Service;

import KChat.Entity.VO.HeadMessageVO;
import KChat.Model.HeadMessageModel;
import KChat.Service.RedisCache;

import java.util.List;


public interface IChatMessageService {
    Long createHeadMessage(HeadMessageModel model);
    Long freshHeadMessage(HeadMessageModel model);
    List<HeadMessageVO> getHeadMessages(String userId, RedisCache redis);
}
