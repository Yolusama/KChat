package KChat.Entity.Enum;

public enum UserApplyStatus {
    VERIFYING(1),ACCEPTED(2),IGNORED(3),REFUSED(4);
    private final int val;
    UserApplyStatus(int val){
        this.val = val;
    }

    public int value(){
        return val;
    }
}
