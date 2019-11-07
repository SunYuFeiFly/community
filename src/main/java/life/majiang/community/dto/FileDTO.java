package life.majiang.community.dto;

import lombok.Data;

/**
 * 文件上传
 */
@Data
public class FileDTO {
    private int success;
    private String message;
    private String url;
}
