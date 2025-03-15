package KChat.Entity.Enum;

public enum UserRole {
    COMMON(1),ADMIN(2);
    private final int val;
    UserRole(int val){
        this.val = val;
    }
    public int value(){return val;}
}
