package KChat.Model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ChatMessageModel {
    private String userId;
    private String contactId;
    private String content;
    private Date time;
    private Integer type;
    private String fileName;
    private Long fileSize;
    private String filePath;
    private Boolean downloaded;
}
