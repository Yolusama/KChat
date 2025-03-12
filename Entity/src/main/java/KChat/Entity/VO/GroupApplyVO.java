package KChat.Entity.VO;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class GroupApplyVO {
    private Long applyId;
    private String groupId;
    private String groupAvatar;
    private String groupName;
    private Integer status;
    private Integer contactStatus;
    private Date time;
    private String info;
    private Integer groupMemberCount;
    private Integer groupSize;
}
