package KChat.Model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class HeadMessageModel {
    private String userId;
    private String contactId;
    private String content;
    private Date time;
}
