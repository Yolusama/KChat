package KChat.Entity.Enum;

public enum MessageType {
    COMMON(1),PICTURE(2),FILE(3);
    private final int val;
    MessageType(int val){
        this.val = val;
    }

    public int value(){
        return val;
    }
}
