package KChat.Common;


public class Pair<T1,T2>{
    private T1 item1;
    private T2 item2;

    public Pair(){}
    public Pair(T1 item1,T2 item2){
        this.item1 = item1;
        this.item2 = item2;
    }
    public static<T1,T2> Pair<T1,T2> makePair(T1 item1,T2 item2){
        return new Pair<>(item1,item2);
    }

    public T1 getItem1() {
        return item1;
    }

    public void setItem1(T1 item1) {
        this.item1 = item1;
    }

    public T2 getItem2() {
        return item2;
    }

    public void setItem2(T2 item2) {
        this.item2 = item2;
    }
}
