package KChat.DbOption.Mapper;

import KChat.Entity.GroupMessageRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface GroupMessageRecordMapper extends BaseMapper<GroupMessageRecord> {
    void batchInsert(@Param("records") List<GroupMessageRecord> records);

    @Update("update GroupMessageRecord set filePath=#{filePath},downloaded=1 where messageId = #{messageId}" +
            " and memberId = #{userId} and groupId =#{groupId} and selfRecord = 0")
    int updateFilePath(@Param("messageId")Long messageId,@Param("userId")String userId,
                       @Param("groupId")String groupId,
                       @Param("filePath")String filePath);
}
