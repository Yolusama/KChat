package KChat.Controller;

import KChat.Annotation.ClearRedisCache;
import KChat.Common.CachingKeys;
import KChat.Common.Constants;
import KChat.DbOption.Service.IUserContactService;
import KChat.DbOption.Service.IUserService;
import KChat.DbOption.ServiceImpl.UserContactService;
import KChat.DbOption.ServiceImpl.UserService;
import KChat.Entity.Enum.UserLoginStatus;
import KChat.Entity.VO.ContactLabelVO;
import KChat.Entity.VO.UserInfoVO;
import KChat.Entity.VO.UserLoginVO;
import KChat.Entity.VO.UserVO;
import KChat.Model.UserContactModel;
import KChat.Model.UserLoginModel;
import KChat.Model.UserRegModel;
import KChat.Model.UserTokenModel;
import KChat.Result.ActionResult;
import KChat.Service.EmailService;
import KChat.Service.JwtService;
import KChat.Service.RedisCache;
import KChat.Utils.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/Api/User")
public class UserController extends ControllerBase{
    private final IUserService userService;
    private final IUserContactService userContactService;
    private final JwtService jwtService;
    private final EmailService emailService;

    @Autowired
    public UserController(UserService userService, UserContactService userContactService,
                          JwtService jwtService, EmailService emailService, RedisCache redis){
        this.userService = userService;
        this.redis = redis;
        this.userContactService = userContactService;
        this.jwtService = jwtService;
        this.emailService = emailService;
    }

    @PostMapping("/Login")
    public ActionResult<UserLoginVO> Login(@RequestBody UserLoginModel model){
        var res = userService.login(model,jwtService,redis);
        if(res.getItem2() == UserLoginStatus.NO_SUCH_USER)
            return fail(HttpStatus.NOT_FOUND);
        if(res.getItem2() == UserLoginStatus.FAILURE)
            return fail("密码错误！");

        return successWithData(res.getItem1());
    }

    @PutMapping("/Register/{checkCode}")
    public ActionResult<String> Register(@PathVariable String checkCode, @RequestBody UserRegModel model){
        String res = userService.register(model,checkCode,redis);
        if(res==null)
            return fail("该电子邮箱已被注册！");
        if(res.equals(Constants.CheckFailed))
            return fail("验证码错误！");
        if(res.equals(Constants.CheckCodeTimeOut))
            return fail("验证码已过期！");
        return successWithData("注册成功！",res);
    }

    @GetMapping("/GetCheckCode/{length}")
    public ActionResult GetCheckCode(@PathVariable String length,@RequestParam String email){
        Integer count = ObjectUtil.isRequestParamStrNull(length)? null : Integer.parseInt(length);
        if(count == null)
            return fail(HttpStatus.BAD_REQUEST);
        Boolean res = userService.getCheckCode(email,count,emailService,redis);
        if(res==null)
            return fail("无法使用的电子邮箱！");
        if(!res)
            return fail("验证码1分钟内不得连续获取！");
        return ok();
    }

    @PostMapping("/VerifyToken")
    public ActionResult<Boolean> VerifyToken(@RequestBody UserTokenModel model){
        Boolean res = userService.verifyToken(model.getUserId(),model.getToken(),redis);
        if(res==null)
            return fail("身份验证信息已过期!");
        if(!res)
            return successWithData("身份验证信息错误！", false);
        return successWithData(true);
    }

    @GetMapping("/GetUserInfo/{userId}")
    public CompletableFuture<ActionResult<UserInfoVO>> GetUserInfo(@PathVariable String userId,
                                                                   @RequestParam String identifier){
        return CompletableFuture.completedFuture(
                successWithData(userService.getUserInfo(userId,identifier))
        );
    }

    @GetMapping("/GetFriends/{userId}")
    public CompletableFuture<ActionResult<Map<String,List<UserInfoVO>>>> GetFriends(@PathVariable String userId){
        return CompletableFuture.completedFuture(
                successWithData(userContactService.getFriends(userId,redis))
        );
    }

    @GetMapping("/SearchUser/{userId}")
    public CompletableFuture<ActionResult<UserVO>> SearchUser(@PathVariable String userId,@RequestParam String identifier){
        var res = userService.searchUser(userId,identifier);
        return CompletableFuture.completedFuture(
                successWithData(res)
        );
    }

    @PutMapping("/MakeFriends")
    public ActionResult MakeFriends(@RequestBody UserContactModel model){
        userContactService.makeFriends(model);
        return ok();
    }

    @GetMapping("/GetUserLabels/{userId}")
    public CompletableFuture<ActionResult<List<ContactLabelVO>>> GetUserLabels(@PathVariable String userId){
        return CompletableFuture.completedFuture(
          successWithData(userContactService.getUserLabels(userId,redis))
        );
    }

    @PutMapping("/CreateLabel/{userId}")
    @ClearRedisCache(keys = CachingKeys.GetUserLabels)
    public ActionResult<ContactLabelVO> CreateLabel(@PathVariable String userId, @RequestParam String labelName,
                                                    HttpServletRequest request){
        return successWithData(userContactService.createLabel(userId,labelName));
    }

    @GetMapping("/IsOnline/{userId}")
    public ActionResult<Boolean> IsOnline(@PathVariable String userId){
        Boolean res = userService.isOnline(userId);
        if(res==null)
            return fail(HttpStatus.NOT_FOUND);
        return successWithData(res);
    }

    @PatchMapping("/GoOffline/{userId}")
    public ActionResult GoOffline(@PathVariable String userId){
        int res = userService.goOffline(userId);
        if(res == Constants.AbnormalState)
            return fail();
        return ok("已下线!");
    }

    @RequestMapping("/TestRegister")
    public ActionResult<String> TestRegister(){
        return successWithData(userService.register());
    }
}
