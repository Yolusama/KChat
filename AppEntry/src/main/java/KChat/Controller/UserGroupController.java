package KChat.Controller;

import KChat.DbOption.Service.IUserGroupService;
import KChat.DbOption.ServiceImpl.UserGroupService;
import KChat.Model.UserGroupModel;
import KChat.Result.ActionResult;
import KChat.Service.FileService;
import KChat.Service.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public ActionResult<String> CreateGroup(@RequestBody UserGroupModel model){
        return successWithData("群创建完成！",groupService.createGroup(model));
    }

    @PostMapping("/UploadAvatar/{userId}")
    public ActionResult<String> UploadAvatar(@PathVariable String userId,
                                     @RequestPart("file")MultipartFile file,@RequestPart("avatar")String avatar){
        return successWithData(groupService.uploadAvatar(userId,avatar,file,fileService));
    }
}
