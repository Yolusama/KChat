package KChat.Entity.VO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserVO extends UserContactVO{
    private String id;
    private String account;
    private String avatar;
    private String nickname;
    private String remark;
    private String signature;
    private Long labelId;
    private String labelName;
    private Integer acceptMode;
}
