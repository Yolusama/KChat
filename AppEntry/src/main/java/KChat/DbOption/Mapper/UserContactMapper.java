package KChat.DbOption.Mapper;

import KChat.Entity.UserContact;
import KChat.Entity.VO.UserInfoVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserContactMapper extends BaseMapper<UserContact> {
    List<UserInfoVO> getFriends(@Param("userId")String userId);
}
