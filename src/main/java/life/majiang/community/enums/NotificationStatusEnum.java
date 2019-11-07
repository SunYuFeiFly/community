package life.majiang.community.enums;

/**
 * 评论状态
 */
public enum NotificationStatusEnum {
    UNREAD(0), //未读
    READ(1);   //已读
    private int status;

    public int getStatus() {
        return status;
    }

    NotificationStatusEnum(int status) {
        this.status = status;
    }
}
