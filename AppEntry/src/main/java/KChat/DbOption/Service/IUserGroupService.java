package KChat.DbOption.Service;

import KChat.Entity.VO.GroupInfoVO;
import KChat.Model.UserGroupModel;
import KChat.Service.FileService;
import KChat.Service.RedisCache;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface IUserGroupService {
    List<String> getUserGroupIds(String userId, RedisCache redis);
    Map<String,List<GroupInfoVO>> getUserGroups(String userId, RedisCache redis);
    String createGroup(UserGroupModel model);
    String uploadAvatar(String groupId, String avatar, MultipartFile file, FileService fileService);
    GroupInfoVO searchGroup(String userId, String identifier);
    void changeDescription(String groupId,String description);
}
