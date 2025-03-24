package KChat.Entity.VO;

import KChat.Common.Constants;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class HeadMessageVO {
    private Long id;
    private String content;
    private String userName;
    private String contactId;
    private String contactName;
    private String contactAvatar;
    private Integer unReadCount = Constants.None;
    private Date time;
}
