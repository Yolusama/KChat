package KChat.Entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("GroupMessageRecord")
public class GroupMessageRecord {
    /**
     * 表id,自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 关联的消息记录id
     */
    private Long recordId;
    /**
     * 成员id
     */
     private String memberId;
}
