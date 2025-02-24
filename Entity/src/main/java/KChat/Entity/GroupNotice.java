package KChat.Entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@TableName("GroupNotice")
@Data
public class GroupNotice{
    /**
    * 群公告id，自增
    */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
    * 群聊id
    */
    private String groupId;
    /**
    * 内容
    */
    private String content;
    /**
    * 创建时间
    */
    private Date createTime;
    /**
    * 更新时间
    */
    private Date updateTime;
}
