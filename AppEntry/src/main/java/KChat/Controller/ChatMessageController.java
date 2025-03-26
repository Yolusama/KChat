package KChat.Controller;

import KChat.Common.Pair;
import KChat.DbOption.Service.IChatMessageService;
import KChat.DbOption.ServiceImpl.ChatMessageService;
import KChat.Entity.VO.ChatMessageVO;
import KChat.Entity.VO.HeadMessageVO;
import KChat.Entity.VO.PagedData;
import KChat.Model.ChatMessageModel;
import KChat.Model.HeadMessageModel;
import KChat.Result.ActionResult;
import KChat.Service.FileService;
import KChat.Service.MQMsgProducer;
import KChat.Service.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/Api/Chat")
public class ChatMessageController extends ControllerBase{
    private final IChatMessageService chatMessageService;
    private final FileService fileService;
    private final MQMsgProducer msgProducer;

    @Autowired
    public ChatMessageController(ChatMessageService chatMessageService,
                                 FileService fileService,
                                 MQMsgProducer msgProducer,
                                 RedisCache redis){
        this.chatMessageService = chatMessageService;
        this.fileService = fileService;
        this.msgProducer = msgProducer;
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
                successWithData(chatMessageService.getChatMessages(page,pageSize,userId,contactId,fileService)
        ));
    }

    @PutMapping("/CreateMessage")
    public ActionResult<Long> CreateMessage(@RequestBody ChatMessageModel model){
        return successWithData(chatMessageService.createMessage(model,msgProducer));
    }

    @PutMapping("/CreateOfflineMessage")
    public ActionResult CreateOfflineMessage(@RequestBody ChatMessageModel model){
        chatMessageService.createOfflineMessage(model);
        return ok();
    }

    @PostMapping("/UploadFile")
    public ActionResult<Pair<String,String>> UploadFile(@RequestPart("file") MultipartFile file,
                                         @RequestPart("suffix") String suffix){
        return successWithData(chatMessageService.uploadFile(suffix,file,fileService));
    }

    @GetMapping("/GetCacheFile")
    public HttpEntity<byte[]> GetCacheFile(@RequestParam String fileName){
        return fileResult(fileName,fileService.getCacheFileBytes(fileName));
    }
}
