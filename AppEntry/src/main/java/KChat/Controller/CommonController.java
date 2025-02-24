package KChat.Controller;

import KChat.Result.ActionResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/Api/Common")
public class CommonController extends ControllerBase{
    @GetMapping("/Heartbeat")
    public ActionResult Heartbeat(){
        return ok("至少心态请求...");
    }
}
