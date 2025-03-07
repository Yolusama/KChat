package KChat.DbOption.Service;


import KChat.Entity.VO.UserInfoVO;
import KChat.Entity.VO.UserVO;
import KChat.Model.UserContactModel;
import KChat.Service.RedisCache;

import java.util.List;
import java.util.Map;

public interface IUserContactService {
    Map<Long, List<UserInfoVO>> getFriends(String userId, RedisCache redis);
    void makeFriends(UserContactModel model);
}
