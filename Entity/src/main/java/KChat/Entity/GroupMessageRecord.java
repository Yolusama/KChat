package KChat.Entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.apache.ibatis.type.JdbcType;

@TableName("GroupMessageRecord")
@Data
public class GroupMessageRecord{
    /**
    * 表id,自增
    */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
    * 用户id
    */
    private String userId;
    /**
     *成员id
     */
    private String memberId;
    /**
    * 群id
    */
    private String groupId;
    /**
    * 消息id
    */
    private Long messageId;
    /**
    * 本地保存/上传的路径
    */
    private String filePath;
    /**
     * 文件是否已被下载，1.是，0.否;发送方恒为1
     */
    @TableField(jdbcType = JdbcType.TINYINT)
    private Boolean downloaded;
    /**
     * 是否为用户自己的记录，1.是，0.否
     */
    @TableField(jdbcType = JdbcType.TINYINT)
    private Boolean selfRecord;
}
