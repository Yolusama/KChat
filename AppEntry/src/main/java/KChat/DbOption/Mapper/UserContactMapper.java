package KChat.DbOption.Mapper;

import KChat.Entity.UserContact;
import KChat.Entity.VO.UserInfoVO;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface UserContactMapper extends BaseMapper<UserContact> {
    List<UserInfoVO> getFriends(@Param("userId")String userId);

    @Update("update UserContact set ${ew.sqlSet} ${ew.customSqlSegment}")
    int update(@Param(Constants.WRAPPER) Wrapper<UserContact> wrapper);
}
