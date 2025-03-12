package KChat.DbOption.Mapper;

import KChat.Entity.User;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    Integer isFriend(@Param("userId")String userId,@Param("contactId")String contactId);

    @Update("update User set ${ew.sqlSet} ${ew.customSqlSegment}")
    int update(@Param(Constants.WRAPPER) Wrapper<User> wrapper);
    @Select("select offline from User where id=#{userId}")
    Boolean isOnline(@Param("userId")String userId);
}
