package KChat.Entity;

import KChat.Common.Constants;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.apache.ibatis.type.JdbcType;

@TableName("ChatMessage")
@Data
public class ChatMessage {
    /**
    * 聊天信息id,自增
    */
   @TableId(type = IdType.AUTO)
    private Long id;
    /**
    * 发送信息的用户id
    */
    private String userId;
    /**
    * 接受消息的对象id
    */
    private String contactId;
    /**
    * 消息类型，1.普通文本，2.图片，3.文件
    */
    private Integer type;
    /**
    * 消息内容
    */
    private String content;
    /**
    * 文件名
    */
    private String fileName;
    /**
    * 文件大小
    */
    private Long fileSize;

    public boolean isGroup(){
     return contactId.indexOf(Constants.GroupIdPrefix)>=Constants.None;
    }
}
