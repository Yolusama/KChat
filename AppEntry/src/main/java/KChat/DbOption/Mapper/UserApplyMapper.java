package KChat.DbOption.Mapper;

import KChat.Entity.UserApply;
import KChat.Entity.VO.UserApplyVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserApplyMapper extends BaseMapper<UserApply> {
    List<UserApplyVO> getUserApplies(@Param("userId")String userId);
}