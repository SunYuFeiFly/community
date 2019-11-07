package life.majiang.community.model;

import lombok.Data;

@Data
public class Notification {

    private Long id;                    //信息id
    private Long notifier;             //信息发送者
    private Long receiver;             //信息接受者
    private Long outerid;              //主题、回复的id
    private Integer type;              //类型（回复、点赞）
    private Long gmtCreate;            //创建时间
    private Integer status;            //状态(0为未读，1为已读)
    private String notifierName;      //信息发送者姓名
    private String outerTitle;        //主题

}
