package KChat.Controller;

import KChat.Annotation.ClearRedisCache;
import KChat.Common.CachingKeys;
import KChat.DbOption.Service.IUserContactService;
import KChat.DbOption.ServiceImpl.UserContactService;
import KChat.Result.ActionResult;
import KChat.Service.RedisCache;
import KChat.Service.SSEService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


@RestController
@RequestMapping("/Api/Common")
public class CommonController extends ControllerBase{
    private final SSEService sseService;
    private final IUserContactService contactService;

    @Autowired
    public CommonController(SSEService sseService, UserContactService userContactService, RedisCache redis){
        this.sseService = sseService;
        this.contactService = userContactService;
        this.redis = redis;
    }

    @GetMapping("/Heartbeat")
    public ActionResult Heartbeat(){
        return ok("这是心跳请求...");
    }

    @GetMapping(value = "/SSE/{userId}",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter Subscribe(@PathVariable String userId) {
        return sseService.subscribe(userId);
    }

    @PostMapping("/ChangeRemark/{userId}/{contactId}")
    @ClearRedisCache(keys = {CachingKeys.GetUserFriends})
    public ActionResult ChangeRemark(@PathVariable String userId,@PathVariable String contactId,
                                     @RequestParam String remark){
        contactService.changeRemark(userId,contactId,remark);
        return ok("已修改备注！");
    }
}
