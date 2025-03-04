package KChat.Model;

import java.util.List;

public class ArrayDataModel<T> {
    private List<T> data;

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
