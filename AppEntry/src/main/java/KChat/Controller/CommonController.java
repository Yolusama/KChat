package KChat.Controller;

import KChat.Result.ActionResult;
import KChat.Service.MQMsgProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/Api/Common")
public class CommonController extends ControllerBase{
    @Autowired
    private MQMsgProducer sender;
    @GetMapping("/Heartbeat")
    public ActionResult Heartbeat(){
        sender.sendMessage("Da");
        return ok("这是心跳请求...");
    }

}
