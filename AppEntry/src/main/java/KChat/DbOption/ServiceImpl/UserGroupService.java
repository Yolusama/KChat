package KChat.DbOption.ServiceImpl;

import KChat.Common.CachingKeys;
import KChat.Common.Constants;
import KChat.DbOption.Mapper.GroupNoticeMapper;
import KChat.DbOption.Mapper.UserGroupMapper;
import KChat.DbOption.Service.IUserGroupService;
import KChat.Model.ArrayDataModel;
import KChat.Service.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserGroupService implements IUserGroupService {
    private final UserGroupMapper groupMapper;
    private final GroupNoticeMapper noticeMapper;

    @Autowired
    public UserGroupService(UserGroupMapper groupMapper,GroupNoticeMapper noticeMapper){
        this.groupMapper = groupMapper;
        this.noticeMapper = noticeMapper;
    }

    @Override
    public List<String> getUserGroups(String userId, RedisCache redis) {
        ArrayDataModel<String> model;
        String key = String.format("%s_%s",userId, CachingKeys.GetUserGroups);
        if(redis.has(key))
            model = (ArrayDataModel<String>) redis.get(key);
        else{
            model = new ArrayDataModel<>();
            model.setData(groupMapper.getUserGroups(userId));
            redis.set(key,model,Constants.UserGroupsGetExpire);
        }
        return model.getData();
    }
}
