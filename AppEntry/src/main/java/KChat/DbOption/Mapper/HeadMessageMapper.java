package KChat.DbOption.Mapper;

import KChat.Entity.HeadMessage;
import KChat.Entity.VO.HeadMessageVO;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface HeadMessageMapper extends BaseMapper<HeadMessage> {
    Integer batchInsert(@Param("messages")List<HeadMessage> messages);
    List<HeadMessageVO> getHeadMessages(@Param("userId")String userId);

    @Update("update HeadMessage set ${ew.sqlSet} ${ew.customSqlSegment}")
    int update(@Param(Constants.WRAPPER) Wrapper<HeadMessage> wrapper);
}
