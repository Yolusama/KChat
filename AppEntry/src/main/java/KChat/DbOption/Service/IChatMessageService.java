package KChat.DbOption.Service;

import KChat.Entity.VO.ChatMessageVO;
import KChat.Entity.VO.HeadMessageVO;
import KChat.Entity.VO.PagedData;
import KChat.Model.ChatMessageModel;
import KChat.Model.HeadMessageModel;
import KChat.Model.MessageRecordModel;
import KChat.Service.FileService;
import KChat.Service.MQMsgProducer;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface IChatMessageService {
    Long createHeadMessage(HeadMessageModel model);
    Long freshHeadMessage(HeadMessageModel model);
    void toSendMessage(String userId,String contactId);
    List<HeadMessageVO> getHeadMessages(String userId);
    PagedData<ChatMessageVO> getChatMessages(Integer page,Integer pageSize,String userId,String contactId,
                                             FileService fileService);
    Long createMessage(ChatMessageModel model,MQMsgProducer msgProducer);
    void createOfflineMessage(ChatMessageModel model);
    String uploadFile (String suffix, MultipartFile file, FileService fileService);
    void updateFilePath(MessageRecordModel model,MQMsgProducer msgProducer);
    void removeMessages(String userId,String contactId);
    void removeMsgRecord(Long recordId,String contactId);
}
