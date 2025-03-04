package KChat.Entity.Enum;

public enum UserLoginStatus {
    SUCCESS(1),FAILURE(2),NO_SUCH_USER(3);

    private final int val;
    UserLoginStatus(int val){
        this.val = val;
    }

    public int value(){
        return val;
    }
}
