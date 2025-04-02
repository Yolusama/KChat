package KChat.Model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContactStatusModel {
    private String userId;
    private String contactId;
    private Integer status;
}
