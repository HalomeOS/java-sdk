package com.guangyuyun.sdk.exception;

/**
 * GuangYuYun SDK异常
 */
public class GuangYuYunException extends Exception {
    
    private Integer code;
    private String message;
    private Integer fileIndex;
    
    public GuangYuYunException(String message) {
        super(message);
        this.message = message;
    }
    
    public GuangYuYunException(Integer code, String message,Integer fileIndex) {
        super(message);
        this.code = code;
        this.message = message;
        this.fileIndex=fileIndex;
    }
    
    public GuangYuYunException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }
    
    public GuangYuYunException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }
    
    public Integer getCode() {
        return code;
    }
    
    public void setCode(Integer code) {
        this.code = code;
    }
    
    @Override
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    @Override
    public String toString() {
        return "GuangYuYunException{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", fileIndex=" + fileIndex +
                '}';
    }

    public Integer getFileIndex() {
        return fileIndex;
    }
}
