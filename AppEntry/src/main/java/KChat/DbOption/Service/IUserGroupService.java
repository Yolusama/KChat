package KChat.DbOption.Service;

import KChat.Entity.VO.GroupVO;
import KChat.Model.UserGroupModel;
import KChat.Service.FileService;
import KChat.Service.RedisCache;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IUserGroupService {
    List<String> getUserGroupIds(String userId, RedisCache redis);
    List<GroupVO> getUserGroups(String userId,RedisCache redis);
    String createGroup(UserGroupModel model);
    String uploadAvatar(String userId, String avatar, MultipartFile file, FileService fileService);
}
