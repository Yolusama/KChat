package KChat.Model;

import java.util.Date;

public class HeadMessageModel {
    private String userId;
    private String contactId;
    private String content;
    private Boolean isVerification;
    private Date time;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getIsVerification() {
        return isVerification;
    }

    public void setIsVerification(Boolean verification) {
        isVerification = verification;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
