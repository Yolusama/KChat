package KChat.Entity.VO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserApplyVO {
    private String contactId;
    private String contactName;
    private String contactAvatar;
    private String applyId;
    private String time;
    private Integer status;
    private String info;
}
