package life.majiang.community.dto;

import life.majiang.community.model.User;
import lombok.Data;

/**
 * 封装通知消息
 */
@Data
public class NotificationDTO {

    private Long id;
    private Long gmtCreate;
    private Integer status;
    private Long notifier;
    private String notifierName;
    private String outerTitle;
    private Long outerid;
    private String typeName;
    private Integer type;

}
