package KChat.DbOption.Mapper;

import KChat.Entity.GroupMessageRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GroupMessageRecordMapper extends BaseMapper<GroupMessageRecord> {
    void batchInsert(@Param("records") List<GroupMessageRecord> records);
}
