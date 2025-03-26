package KChat.Entity;

import KChat.Common.Constants;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.apache.ibatis.type.JdbcType;

import java.util.Date;

@TableName("ChatMessage")
@Data
public class ChatMessage {
    /**
    * 表id，自增
    */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 消息类型，1.普通文本，2.图片，3.视频，4.文件
     */
    private Integer type;
    /**
     * 消息内容
     */
    private String content;
    /**
     * 消息是否被处理，1.是，0.否
     */
    @TableField(jdbcType = JdbcType.TINYINT)
    private Boolean handled;
    /**
    * 文件名称
    */
    private String fileName;
    /**
    * 文件大小
    */
    private Long fileSize;
    /**
    * 发送方的文件原路径/接收方的文件储存路径，默认值为浏览器下载的路径
    */
    private String filePath;
    /**
    * 消息收到/成功发送的时间
    */
    private Date time;
}
