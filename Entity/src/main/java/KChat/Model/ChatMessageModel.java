package KChat.Model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageModel {
    private String userId;
    private String contactId;
    private String content;
    private String time;
    private Integer type;
    private String fileName;
    private String fileSize;
}
