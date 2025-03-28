package KChat.DbOption.Mapper;

import KChat.Entity.MessageRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface MessageRecordMapper extends BaseMapper<MessageRecord> {
    void batchInsert(@Param("records") List<MessageRecord> records);

    @Update("update MessageRecord set filePath=#{filePath},downloaded=1 where messageId = #{messageId} and userId = #{userId}" +
            " and contactId=#{contactId} and userSent = 0")
    int updateFilePath(@Param("messageId")Long messageId,@Param("userId")String userId,
                        @Param("contactId")String contactId,
                        @Param("filePath")String filePath);
}
