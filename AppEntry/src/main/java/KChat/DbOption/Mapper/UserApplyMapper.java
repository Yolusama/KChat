package KChat.DbOption.Mapper;

import KChat.Entity.UserApply;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserApplyMapper extends BaseMapper<UserApply> {
    @Select("select id,userId,contactId,info,status,time from UserApply where userId=#{userId} order by time desc")
    List<UserApply> getUserApplies(@Param("userId")String userId);
}