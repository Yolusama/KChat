package KChat.DbOption.Mapper;

import KChat.Entity.UserApply;
import KChat.Entity.VO.UserApplyVO;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface UserApplyMapper extends BaseMapper<UserApply> {
    List<UserApplyVO> getUserApplies(@Param("userId")String userId);

    @Update("update HeadMessage set ${ew.sqlSet} ${ew.customSqlSegment}")
    int update(@Param(Constants.WRAPPER) Wrapper<UserApply> wrapper);
}