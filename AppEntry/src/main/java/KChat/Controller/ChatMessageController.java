package KChat.Controller;

import KChat.DbOption.Service.IChatMessageService;
import KChat.DbOption.ServiceImpl.ChatMessageService;
import KChat.Entity.VO.ChatMessageVO;
import KChat.Entity.VO.HeadMessageVO;
import KChat.Entity.VO.PagedData;
import KChat.Model.ChatMessageModel;
import KChat.Model.HeadMessageModel;
import KChat.Result.ActionResult;
import KChat.Service.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/Api/Chat")
public class ChatMessageController extends ControllerBase{
    private final IChatMessageService chatMessageService;

    @Autowired
    public ChatMessageController(ChatMessageService chatMessageService, RedisCache redis){
        this.chatMessageService = chatMessageService;
        this.redis = redis;
    }

    @PutMapping("/CreateHeadMessage")
    public ActionResult<Long> CreateHeadMessage(@RequestBody HeadMessageModel model){
        return successWithData(chatMessageService.createHeadMessage(model));
    }

    @PostMapping("/FreshHeadMessage")
    public ActionResult<Long> FreshHeadMessage(@RequestBody HeadMessageModel model){
        return successWithData(chatMessageService.freshHeadMessage(model));
    }

    @GetMapping("/GetHeadMessages/{userId}")
    public CompletableFuture<ActionResult<List<HeadMessageVO>>> GetHeadMessages(@PathVariable String userId){
        return CompletableFuture.completedFuture(successWithData(
                chatMessageService.getHeadMessages(userId)
        ));
    }

    @GetMapping("/GetMessages/{userId}/{contactId}")
    public CompletableFuture<ActionResult<PagedData<ChatMessageVO>>> GetMessages(@PathVariable String userId,
                                                                                 @PathVariable String contactId,
                                                                                 @RequestParam Integer page,
                                                                                 @RequestParam Integer pageSize){
        return CompletableFuture.completedFuture(
                successWithData(chatMessageService.getChatMessages(page,pageSize,userId,contactId)
        ));
    }

    @PutMapping("/CreateMessage")
    public ActionResult<Long> CreateMessage(@RequestBody ChatMessageModel model){
        return successWithData(chatMessageService.createMessage(model));
    }

}
