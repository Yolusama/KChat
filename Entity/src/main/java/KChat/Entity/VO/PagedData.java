package KChat.Entity.VO;

public class PagedData<T> {
    private T[] data;
    private Long total;

    public PagedData(){}

    public PagedData(T[] data,Long total){
        this.data = data;
        this.total = total;
    }

    public T[] getData() {
        return data;
    }

    public Long getTotal() {
        return total;
    }

    public void setData(T[] data) {
        this.data = data;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
