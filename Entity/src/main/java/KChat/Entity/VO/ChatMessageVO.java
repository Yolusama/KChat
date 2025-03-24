package KChat.Entity.VO;

import KChat.Common.Constants;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ChatMessageVO {
    private Long id;
    private String userId;
    private String contactId;
    private String contactName;
    private String contactAvatar;
    private Integer type;
    private String content;
    private String fileName;
    private Long fileSize;
    private Date time;

    public Boolean fileTimeOut(){
        if(fileName == null)return null;
        return (Constants.now().getTime() - time.getTime()) >= Constants.FileCachedExpire.toMillis();
    }
}
