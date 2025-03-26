package KChat.DbOption.Mapper;

import KChat.Entity.MessageRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MessageRecordMapper extends BaseMapper<MessageRecord> {
    void batchInsert(@Param("records") List<MessageRecord> records);
}
