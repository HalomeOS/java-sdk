package com.guangyuyun.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 小文件上传响应结果
 */
public class SmallFileUploadResponse {
    
    @JsonProperty("code")
    private Integer code;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("data")
    private String data; // 文件ID
    
    public SmallFileUploadResponse() {
    }
    
    public Integer getCode() {
        return code;
    }
    
    public void setCode(Integer code) {
        this.code = code;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getData() {
        return data;
    }
    
    public void setData(String data) {
        this.data = data;
    }
    
    /**
     * 获取文件ID（data字段的别名）
     */
    public String getFileId() {
        return data;
    }
    
    /**
     * 判断请求是否成功
     */
    public boolean isSuccess() {
        return code != null && code == 200;
    }
    
    @Override
    public String toString() {
        return "SmallFileUploadResponse{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}

