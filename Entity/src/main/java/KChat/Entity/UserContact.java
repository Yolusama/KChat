package KChat.Entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.apache.ibatis.type.JdbcType;

import java.util.Date;

@TableName("UserContact")
@Data
public class UserContact {

    /**
     * 用户联系表id,自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 用户id
     */
    private String userId;
    /**
     * 联系对象id
     */
    private String contactId;
    /**
     * 备注
     */
    private String remark;
    /**
     * 联系人状态，为用户1.正常，2.拉黑，3.删除，4.被拉黑，5.被删除；为群组时：1.正常，2.被踢出群聊
     */
    private Integer status;
    /**
     * 联系人标签
     */
    private Long labelId;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
}
