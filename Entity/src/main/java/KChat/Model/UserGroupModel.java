package KChat.Model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserGroupModel {
    private String name;
    private String avatar;
    private String ownerId;
    private Integer size;
    private Integer acceptMode;
}
