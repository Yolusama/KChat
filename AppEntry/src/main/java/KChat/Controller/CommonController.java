package KChat.Controller;

import KChat.Result.ActionResult;
import KChat.Service.RedisCache;
import KChat.Service.SSEService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/Api/Common")
public class CommonController extends ControllerBase{
    private final SSEService sseService;

    @Autowired
    public CommonController(SSEService sseService, RedisCache redis){
        this.sseService = sseService;
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
}
