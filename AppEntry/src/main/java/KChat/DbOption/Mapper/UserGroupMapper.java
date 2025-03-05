package KChat.DbOption.Mapper;

import KChat.Entity.UserGroup;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserGroupMapper extends BaseMapper<UserGroup> {
    @Select("select g.id from UserGroup g join UserContact uc on g.id = uc.contactId where uc.isGroup=1" +
            " and uc.userId=#{userId}")
    List<String> getUserGroups(@Param("userId")String userId);
}
