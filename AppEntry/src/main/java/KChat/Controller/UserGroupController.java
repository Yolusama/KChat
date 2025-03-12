package KChat.Controller;

import KChat.Annotation.ClearRedisCache;
import KChat.Common.CachingKeys;
import KChat.DbOption.Service.IUserGroupService;
import KChat.DbOption.ServiceImpl.UserGroupService;
import KChat.Entity.VO.GroupVO;
import KChat.Entity.VO.UserVO;
import KChat.Model.UserGroupModel;
import KChat.Result.ActionResult;
import KChat.Service.FileService;
import KChat.Service.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/Api/Group")
public class UserGroupController extends ControllerBase{
    private final IUserGroupService groupService;
    private final FileService fileService;

    @Autowired
    public UserGroupController(UserGroupService groupService, RedisCache redis,FileService fileService){
        this.groupService = groupService;
        this.redis = redis;
        this.fileService = fileService;
    }

    @PutMapping("/CreateGroup")
    @ClearRedisCache(keys = {CachingKeys.GetUserGroupIds,CachingKeys.GetUserGroups})
    public ActionResult<String> CreateGroup(@RequestBody UserGroupModel model){
        return successWithData("群创建完成！",groupService.createGroup(model));
    }

    @PostMapping("/UploadAvatar/{userId}")
    @ClearRedisCache(keys = {CachingKeys.GetUserGroups})
    public ActionResult<String> UploadAvatar(@PathVariable String userId,
                                     @RequestPart("file")MultipartFile file,@RequestPart("avatar")String avatar){
        return successWithData(groupService.uploadAvatar(userId,avatar,file,fileService));
    }

    @GetMapping("/GetGroups/{userId}")
    public CompletableFuture<ActionResult<List<GroupVO>>> GetUserGroups(@PathVariable String userId){
        return CompletableFuture.completedFuture(
                successWithData(groupService.getUserGroups(userId,redis))
        );
    }
}
