package KChat.Model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserApplyModel {
    private String userId;
    private String contactId;
    private Integer acceptMode;
    private String info;
}
