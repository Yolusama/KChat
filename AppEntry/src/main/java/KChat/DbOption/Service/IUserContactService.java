package KChat.DbOption.Service;


import KChat.Entity.VO.ContactLabelVO;
import KChat.Entity.VO.UserInfoVO;
import KChat.Model.UserContactModel;
import KChat.Service.RedisCache;

import java.util.List;
import java.util.Map;

public interface IUserContactService {
    Map<Long, List<UserInfoVO>> getFriends(String userId, RedisCache redis);
    void makeFriends(UserContactModel model);
    List<ContactLabelVO> getUserLabels(String userId,RedisCache redis);
    ContactLabelVO createLabel(String userId,String labelName);
}
