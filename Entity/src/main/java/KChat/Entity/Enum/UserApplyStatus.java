package KChat.Entity.Enum;

public enum UserApplyStatus {
    VERIFY(1),IGNORE(2),REFUSE(3);
    private final int val;
    UserApplyStatus(int val){
        this.val = val;
    }

    public int value(){
        return val;
    }
}
