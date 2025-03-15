package KChat.DbOption.Mapper;

import KChat.Entity.UserGroup;
import KChat.Entity.VO.GroupVO;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface UserGroupMapper extends BaseMapper<UserGroup> {
    List<GroupVO> getGroups(@Param("userId")String userId);

    @Select("select g.id from UserGroup g join GroupContact gc on g.id = gc.groupId where gc.userId=#{userId}")
    List<String> getUserGroupIds(@Param("userId")String userId);
    @Update("update UserGroup set ${ew.sqlSet} ${ew.customSqlSegment}")
    int update(@Param(Constants.WRAPPER) Wrapper<UserGroup> wrapper);
}
