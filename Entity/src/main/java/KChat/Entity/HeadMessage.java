package KChat.Entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.apache.ibatis.type.JdbcType;

import java.util.Date;

@TableName("HeadMessage")
@Data
public class HeadMessage{
    /**
    * 头信息id,自增
    */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
    * 展示内容
    */
    private String content;
    /**
    * 用户Id
    */
    private String userId;
    /**
     * 消息联系对象id
     */
    private String contactId;
    /**
    * 最后一次聊天的时间
    */
    private Date time;
}
