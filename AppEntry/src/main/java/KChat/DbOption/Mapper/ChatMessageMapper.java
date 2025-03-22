package KChat.DbOption.Mapper;

import KChat.Entity.ChatMessage;
import KChat.Entity.VO.ChatMessageVO;
import KChat.Entity.VO.MsgUnReadVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {
    List<ChatMessageVO> getChatMessages(Page<ChatMessageVO> page, @Param("userId")String userId,
                                        @Param("contactId")String contactId);
    List<ChatMessageVO> getGroupMessages(Page<ChatMessageVO> page, @Param("userId")String userId,
                                        @Param("groupId")String groupId);
    List<MsgUnReadVO> getUnReadCounts(@Param("userId")String userId);
}
