package KChat.Entity.VO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserApplyVO {
    private String userId;
    private String userName;
    private String userAvatar;
    private String contactId;
    private String contactName;
    private String contactAvatar;
    private Long applyId;
    private String time;
    private Integer status;
    private String info;
}
