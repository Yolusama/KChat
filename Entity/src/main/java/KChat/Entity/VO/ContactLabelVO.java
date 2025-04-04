package KChat.Entity.VO;

import java.util.Objects;

public class ContactLabelVO {
    private Long id;
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString(){
        return String.format("{\"id\":%d,\"name\":\"%s\"}",id,name);
    }
}
