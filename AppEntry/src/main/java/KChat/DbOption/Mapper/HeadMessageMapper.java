package KChat.DbOption.Mapper;

import KChat.Entity.HeadMessage;
import KChat.Entity.VO.HeadMessageVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface HeadMessageMapper extends BaseMapper<HeadMessage> {
    Integer batchInsert(@Param("messages")List<HeadMessage> messages);
    List<HeadMessageVO> getHeadMessages(@Param("userId")String userId);
}
