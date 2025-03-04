package KChat.DbOption.Service;

import KChat.Entity.VO.ChatMessageVO;
import KChat.Entity.VO.HeadMessageVO;
import KChat.Entity.VO.PagedData;
import KChat.Model.ChatMessageModel;
import KChat.Model.HeadMessageModel;
import KChat.Model.UserApplyModel;

import java.util.List;


public interface IChatMessageService {
    Long createHeadMessage(HeadMessageModel model);
    Long freshHeadMessage(HeadMessageModel model);
    List<HeadMessageVO> getHeadMessages(String userId);
    PagedData<ChatMessageVO> getChatMessages(Integer page,Integer pageSize,String userId,String contactId);
    void makeApply(UserApplyModel model);
    Long createMessage(ChatMessageModel model);
}
