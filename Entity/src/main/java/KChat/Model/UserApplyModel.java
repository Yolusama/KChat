package KChat.Model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserApplyModel {
    private String userId;
    private String contactId;
    private Integer acceptMode;
    private Integer status;
    private Long labelId;
    private Long contactLabelId;
    private String info;
    private String groupOwnerId;
}
