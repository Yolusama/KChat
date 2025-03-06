package KChat.Entity.Enum;

public enum UserAcceptType {
    DIRECT(1),NEED_APPLY(2);
    private final int val;
    UserAcceptType(int val){
        this.val = val;
    }

    public int value(){
        return val;
    }
}
