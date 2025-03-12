package KChat.Entity.Enum;

public enum UserGroupStatus {
    NORMAL(1),KICKED_OUT(2),DISMISSED(3);
    private final int val;
    private UserGroupStatus(int val){
        this.val = val;
    }

    public int value(){return val;}
}
