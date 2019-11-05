package life.majiang.community.enums;

public enum  CommentTypeEnum {
    QUESTION(1), //一级评论
    COMMENT(2),  //二级评论
    ;

    private Integer type;

    public Integer getType() {
        return type;
    }

    CommentTypeEnum(Integer type) {
        this.type = type;
    }

    //判断comment类型是否正确
    public static boolean isExit(Integer type) {
        for (CommentTypeEnum commentTypeEnum : CommentTypeEnum.values()) {
            if (commentTypeEnum.getType() == type) {
                return true;
            }
        }
        return false;
    }




}
