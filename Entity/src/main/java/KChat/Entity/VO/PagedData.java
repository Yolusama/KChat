package KChat.Entity.VO;

import java.util.List;

public class PagedData<T> {
    private List<T> data;
    private Long total;

    public PagedData(){}

    public PagedData(List<T> data, Long total){
        this.data = data;
        this.total = total;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
