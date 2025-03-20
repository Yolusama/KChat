package KChat.Entity.VO;

public class MsgUnReadVO {
    private Long headMessageId;
    private Integer unReadCount;

    public Long getHeadMessageId() {
        return headMessageId;
    }

    public void setHeadMessageId(Long headMessageId) {
        this.headMessageId = headMessageId;
    }

    public Integer getUnReadCount() {
        return unReadCount;
    }

    public void setUnReadCount(Integer unReadCount) {
        this.unReadCount = unReadCount;
    }
}
