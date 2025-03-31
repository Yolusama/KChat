package KChat.DbOption.Mapper;

import KChat.Entity.ContactLabel;
import KChat.Entity.VO.ContactLabelVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ContactLabelMapper extends BaseMapper<ContactLabel> {
    @Select("select l.id,l.name from ContactLabel l where l.userId = #{userId}")
    List<ContactLabelVO> getContactLabels(@Param("userId")String userId);
}
