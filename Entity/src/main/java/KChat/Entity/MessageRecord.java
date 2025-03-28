package KChat.Entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.apache.ibatis.type.JdbcType;

@Data
@TableName("MessageRecord")
public class MessageRecord {
    /**
    * 聊天信息id,自增
    */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 发送消息的对象id
     */
    private String userId;
    /**
    * 接受消息的对象id
    */
    private String contactId;
    /**
    * 消息id
    */
    private Long messageId;
    /**
     * 1.用户发送信息，0.用户接受信息
     */
    @TableField(jdbcType = JdbcType.TINYINT)
    private Boolean userSent;
    /**
     * 发送方的文件原路径/接收方的文件储存路径，默认值为浏览器下载的路径
     */
    private String filePath;
    /**
     * 文件是否已被下载，1.是，0.否;发送方恒为1
     */
    @TableField(jdbcType = JdbcType.TINYINT)
    private Boolean downloaded;
}
