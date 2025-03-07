package KChat.Entity.VO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserVO extends UserContactVO{
    private String id;
    private String account;
    private String email;
    private String avatar;
    private String nickname;
    private String signature;
    private Integer acceptMode;
    private Boolean isFriend;
    private String area;
}
