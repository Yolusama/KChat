package KChat.Entity.VO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupInfoVO {
    private String id;
    private String name;
    private String avatar;
    private String ownerId;
    private Integer currentCount;
    private Integer size;
    private String description;
    private String remark;
    private Boolean userJoined;

    public Boolean canJoin(){
        return currentCount < size;
    }
}
