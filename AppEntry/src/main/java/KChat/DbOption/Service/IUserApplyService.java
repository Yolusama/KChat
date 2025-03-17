package KChat.DbOption.Service;


import KChat.Entity.VO.GroupApplyVO;
import KChat.Entity.VO.UserApplyVO;
import KChat.Model.UserApplyModel;
import KChat.Service.MQMsgProducer;
import KChat.Service.RedisCache;

import java.util.List;

public interface IUserApplyService {
    List<UserApplyVO> getUserApplies(String userId, RedisCache redis);
    List<GroupApplyVO> getGroupApplies(String userId,RedisCache redis);
    void makeApply(UserApplyModel model, MQMsgProducer msgProducer);
    int setApplyStatus(UserApplyModel model,MQMsgProducer msgProducer);
    void removeContactorCache(String userId,Boolean isUserApply,RedisCache redis);
}
