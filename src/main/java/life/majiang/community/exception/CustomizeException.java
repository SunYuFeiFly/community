package life.majiang.community.exception;

//处理已知异常
public class CustomizeException extends RuntimeException {

    private String message;

    @Override
    public String getMessage() {
        return message;
    }

    public CustomizeException(ICustomizeErrorCode errorCode) {
        this.message = errorCode.getMessage();
    }
}
