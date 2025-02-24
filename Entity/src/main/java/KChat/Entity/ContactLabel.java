package KChat.Entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@TableName("ContactLabel")
@Data
public class ContactLabel{
    /**
    * 联系人标签id,自增
    */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
    * 标签名称
    */
    private String name;
    /**
    * 用户id
    */
    private String userId;
    /**
    * 创建时间
    */
    private Date createTime;
    /**
    * 更新时间
    */
    private Date updateTime;
}
