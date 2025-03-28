package KChat.Model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageRecordModel {
    private Long messageId;
    private String userId;
    private String contactId;
    private String filePath;
}
