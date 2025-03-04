package KChat.Entity.VO;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ChatMessageVO {
    private Long id;
    private String userId;
    private String contactId;
    private String contactNickName;
    private String contactAvatar;
    private Integer type;
    private String content;
    private String fileName;
    private Long fileSize;
    private Date time;
}
