package KChat.Entity.Enum;

public enum UserContactStatus {
    NORMAL(1),BLOCK(2),REMOVE(3),BLOCKED(4),REMOVED(5);
    private final int val;
    UserContactStatus(int val){
        this.val = val;
    }

    public int value(){
        return val;
    }
}
