package KChat.DbOption.Service;

import KChat.Service.RedisCache;

import java.util.List;

public interface IUserGroupService {
    List<String> getUserGroups(String userId, RedisCache redis);
}
