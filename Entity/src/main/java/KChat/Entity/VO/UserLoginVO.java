package KChat.Entity.VO;


import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class UserLoginVO {
    private String id;
    private String account;
    private String email;
    private String nickname;
    private Integer gender;
    private String avatar;
    private String signature;
    private Date createTime;
    private String area;
    private String areaCode;
    private Integer loginStatus;
    private String token;
}
