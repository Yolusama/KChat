package KChat.Entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@TableName("UserGroup")
@Data
public class UserGroup{
    /**
    * 群聊主键
    */
    @TableId
    private String id;
    /**
    * 群名称
    */
    private String name;
    /**
    * 群头像
    */
    private String avatar;
    /**
    * 群主id
    */
    private String ownerId;
    /**
     * 群状态，1.正常，2.已解散
     */
    private Integer status;
    /**
     * 当前群人数
     */
    private Integer currentCount;
    /**
    * 群规模
    */
    private Integer size;
    /**
    * 进群接受模式，1.无需申请，直接进入，2.需要申请
    */
    private Integer acceptMode;
    /**
    * 创建时间
    */
    private Date createTime;
    /**
    * 更新时间
    */
    private Date updateTime;
    /**
     * 群描述信息
     */
    private String description;
}
