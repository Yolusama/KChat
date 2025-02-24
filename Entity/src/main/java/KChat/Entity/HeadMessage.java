package KChat.Entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

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
    private String userid;
    /**
    * 关联的消息Id
    */
    private Long messageId;
    /**
    * 更新时间
    */
    private Date updateTime;
}
