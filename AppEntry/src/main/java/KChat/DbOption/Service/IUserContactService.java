package KChat.DbOption.Service;


import KChat.Entity.VO.UserVO;
import KChat.Service.RedisCache;

import java.util.List;
import java.util.Map;

public interface IUserContactService {
    Map<Long, List<UserVO>> getFriends(String userId, RedisCache redis);
}
