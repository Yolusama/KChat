package KChat.Controller;

import KChat.DbOption.Service.IUserGroupService;
import KChat.DbOption.ServiceImpl.UserGroupService;
import KChat.Service.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/Api/Group")
public class UserGroupController extends ControllerBase{
    private final IUserGroupService groupService;

    @Autowired
    public UserGroupController(UserGroupService groupService, RedisCache redis){
        this.groupService = groupService;
        this.redis = redis;
    }
}
