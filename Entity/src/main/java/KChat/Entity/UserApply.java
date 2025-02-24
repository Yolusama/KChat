package KChat.Entity;


import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("UserApply")
@Data
public class UserApply {
    /**
    * 用户申请信息维护表id,自增
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
    * 申请状态，1.验证中，2.忽略，3.拒绝
    */
    private Integer status;
    /**
    * 申请时间
    */
    private Date createTime;
}
