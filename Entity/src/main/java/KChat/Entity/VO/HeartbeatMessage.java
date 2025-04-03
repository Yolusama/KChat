package KChat.Entity.VO;

public class HeartbeatMessage {
    private String userId;
    private Boolean isResponse;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Boolean getIsResponse() {
        return isResponse;
    }

    public void setIsResponse(Boolean isResponse) {
        this.isResponse = isResponse;
    }
}
