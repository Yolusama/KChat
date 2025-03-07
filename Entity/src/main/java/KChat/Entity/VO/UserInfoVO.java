package KChat.Entity.VO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfoVO {
    private String account;
    private String email;
    private String nickname;
    private Integer gender;
    private String avatar;
    private String signature;
    private String area;
    private String areaCode;
    private Long labelId;
    private Boolean isFriend;
}
