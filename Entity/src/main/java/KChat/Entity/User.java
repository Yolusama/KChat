package KChat.Entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.apache.ibatis.type.JdbcType;

import java.util.Date;

@TableName("User")
@Data
public class User{
    /**
    * 用户id
    */
    @TableId
    private String id;
    /**
    * 用户账号
    */
    private String account;
    /**
    * 用户电子邮箱
    */
    private String email;
    /**
    * 用户昵称
    */
    private String nickname;
    /**
    * 性别，1：男，0：女
    */
    private Integer gender;
    /**
    * 密码
    */
    private String password;
    /**
    * 用户头像
    */
    private String avatar;
    /**
    * 个性签名
    */
    private String signature;
    /**
    * 用户状态，1：正常，0：异常
    */
    @TableField(jdbcType = JdbcType.TINYINT)
    private Boolean status;
    /**
    * 用户创建时间
    */
    private Date createTime;
    /**
    * 上一次登录时间
    */
    private Date lastLoginTime;
    /**
    * 用户所属地区
    */
    private String area;
    /**
    * 用户所属地区的地区代码
    */
    private String areaCode;
    /**
    * 上一次离线时间
    */
    private Date lastOfflineTime;
    /**
    * 用户角色,1.管理员，2.普通用户
    */
    private Integer role;
    /**
    * 用户是否离线，0：在线，1：离线
    */
    @TableField(jdbcType = JdbcType.TINYINT)
    private Boolean offline;
    /**
    * 添加好友方式：2.需要申请 1.直接可被加为好友
    */
    private Integer acceptMode;
}
