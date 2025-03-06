package KChat.Controller;

import KChat.Common.Constants;
import KChat.DbOption.Service.IUserApplyService;
import KChat.DbOption.ServiceImpl.UserApplyService;
import KChat.Entity.VO.UserApplyVO;
import KChat.Model.UserApplyModel;
import KChat.Result.ActionResult;
import KChat.Service.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/Api/UserApply")
public class UserApplyController extends ControllerBase{
    private final IUserApplyService applyService;

    @Autowired
    public UserApplyController(UserApplyService applyService, RedisCache redis){
        this.applyService = applyService;
        this.redis = redis;
    }

    @GetMapping("/GetUserApplies/{userId}")
    public CompletableFuture<ActionResult<List<UserApplyVO>>> GetUserApplies(@PathVariable String userId){
        return CompletableFuture.completedFuture(
                successWithData(applyService.getUserApplies(userId))
        );
    }

    @PutMapping("/MakeApply")
    public ActionResult MakeApply(@RequestBody UserApplyModel model){
        applyService.makeApply(model);
        return ok("已发出好友申请！");
    }

    @PatchMapping("/SetApplyStatus")
    public ActionResult SetApplyStatus(@RequestBody UserApplyModel model){
        int res = applyService.setApplyStatus(model);
        if(res == Constants.AbnormalState)
            return fail();
        return ok("已正确确定当前申请状态！");
    }
}

