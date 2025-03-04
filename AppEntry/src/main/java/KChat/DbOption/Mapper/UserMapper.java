package KChat.DbOption.Mapper;

import KChat.Entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    Integer isFriend(@Param("userId")String userId,@Param("contactId")String contactId);
}
