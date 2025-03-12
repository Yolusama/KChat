package KChat.Controller;

import KChat.Annotation.ClearRedisCache;
import KChat.Common.CachingKeys;
import KChat.Common.Constants;
import KChat.DbOption.Service.IUserApplyService;
import KChat.DbOption.ServiceImpl.UserApplyService;
import KChat.Entity.VO.GroupApplyVO;
import KChat.Entity.VO.UserApplyVO;
import KChat.Model.UserApplyModel;
import KChat.Result.ActionResult;
import KChat.Service.MQMsgProducer;
import KChat.Service.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/Api/UserApply")
public class UserApplyController extends ControllerBase{
    private final IUserApplyService applyService;
    private final MQMsgProducer msgProducer;

    @Autowired
    public UserApplyController(UserApplyService applyService,MQMsgProducer msgProducer, RedisCache redis){
        this.applyService = applyService;
        this.msgProducer = msgProducer;
        this.redis = redis;
    }

    @GetMapping("/GetUserApplies/{userId}")
    public CompletableFuture<ActionResult<List<UserApplyVO>>> GetUserApplies(@PathVariable String userId){
        return CompletableFuture.completedFuture(
                successWithData(applyService.getUserApplies(userId,redis))
        );
    }

    @PutMapping("/MakeApply")
    @ClearRedisCache(keys = {CachingKeys.GetUserApplies,CachingKeys.GetGroupApplies})
    public ActionResult MakeApply(@RequestBody UserApplyModel model, HttpServletRequest request){
        applyService.makeApply(model,msgProducer);
        return ok("已发出好友申请！");
    }

    @PatchMapping("/SetApplyStatus")
    @ClearRedisCache(keys = {CachingKeys.GetUserApplies,CachingKeys.GetGroupApplies})
    public ActionResult SetApplyStatus(@RequestBody UserApplyModel model,HttpServletRequest request){
        int res = applyService.setApplyStatus(model,msgProducer);
        if(res == Constants.AbnormalState)
            return fail();
        return ok("已正确确定当前申请状态！");
    }

    @GetMapping("/GetGroupApplies/{userId}")
    public CompletableFuture<ActionResult<List<GroupApplyVO>>> GetGroupApplies(@PathVariable String userId){
        return CompletableFuture.completedFuture(
                successWithData(applyService.getGroupApplies(userId,redis))
        );
    }
}

