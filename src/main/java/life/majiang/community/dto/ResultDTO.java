package life.majiang.community.dto;

import life.majiang.community.exception.CustomizeErrorCode;
import life.majiang.community.exception.CustomizeException;
import lombok.Data;

@Data
public class ResultDTO<T> {

    private Integer code;
    private String message;
    private T data;

    public ResultDTO(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public ResultDTO() {
    }

    //用于封装反馈结果
    public static ResultDTO errorOf(Integer code, String message) {
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setCode(code);
        resultDTO.setMessage(message);
        return resultDTO;
    }

    //用于封装CustomizeErrorCode形式错误消息
    public static ResultDTO errorOf(CustomizeErrorCode customizeErrorCode){
        //用于封装反馈结果
        return errorOf(customizeErrorCode.getCode(),customizeErrorCode.getMessage());
    }

    //用于封装CustomizeException形式错误消息
    public static ResultDTO errorOf(CustomizeException customizeException){
        //用于封装反馈结果
        return errorOf(customizeException.getCode(),customizeException.getMessage());
    }

    //用于评论成功反馈
    public static Object okOf() {
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setCode(200);
        resultDTO.setMessage("评论成功！");
        return resultDTO;
    }

    //用于评论失败反馈
    public static Object falseOf() {
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setCode(400);
        resultDTO.setMessage("评论未成功！");
        return resultDTO;
    }

    public static <T> ResultDTO okOf(T t) {
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setCode(200);
        resultDTO.setMessage("请求成功");
        resultDTO.setData(t);
        return resultDTO;
    }

}
