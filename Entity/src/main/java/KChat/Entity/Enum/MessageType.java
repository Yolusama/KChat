package KChat.Entity.Enum;

public enum MessageType {
    COMMON(1),PICTURE(2),VIDEO(3),FILE(4);
    private final int val;
    MessageType(int val){
        this.val = val;
    }

    public int value(){
        return val;
    }
}
