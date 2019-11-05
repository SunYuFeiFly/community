package life.majiang.community.dto;

import lombok.Data;

//提交回复封装数据
@Data
public class CommentCreateDTO {

    private Long parentId;
    private String content;
    private Integer type;
}
