package KChat.Entity.VO;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class HeadMessageVO {
    private Long id;
    private String content;
    private String contactId;
    private String contactNickname;
    private String contactAvatar;
    private Integer unReadCount;
    private Date time;
    private Boolean isVerification;
}
