package KChat.Entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@TableName("GroupContact")
@Data
public class GroupContact{

    /**
    * id,自增
    */
   @TableId(type = IdType.AUTO)
    private Long id;
    /**
    * 用户id
    */
    private String userId;
    /**
    * 群id
    */
    private String groupId;
    /**
    * 群状态，1.正常，2.已解散
    */
    private Integer status;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
    * 更新时间
    */
    private Date updateTime;
}
