package life.majiang.community.model;

import lombok.Data;

@Data
public class Comment {

    private Long id;                 //评论id
    private Long parentId;          //主题id
    private Integer type;           // 判断一级、二级评论
    private Long commentator;      //
    private Long gmtCreate;        //评论时间
    private Long gmtModified;      //评论修改时间
    private Long likeCount;        //评论喜欢数
    private String content;        //评论内容
    private Integer commentCount;      //二级评论数？
}
