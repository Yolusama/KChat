package KChat.Entity;


import com.baomidou.mybatisplus.annotation.TableField;
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
    private String Id;
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
    * 上一次登陆时间
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
    private Long lastOfflineTime;
    /**
    * 用户角色
    */
    private String role;
    /**
    * 登录状态，1：在线，0：离线
    */
    private Integer loginSatus;
    /**
    * 添加好友方式：1.需要申请 0.直接可被加为好友
    */
    private Integer acceptMode;
}
