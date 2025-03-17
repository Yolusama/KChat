package KChat.Model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class SentChatMessageModel {
    private String userId;
    private String contactId;
    private String content;
    private Date time;
    private Integer type;
    private String fileName;
    private String fileSize;
    private String contactAvatar;
    private String contactName;
    private Boolean isVerification;
    private Long applyId;
    private Integer applyStatus;
}
