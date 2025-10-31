package com.guangyuyun.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 大文件上传响应结果
 */
public class LargeFileUploadResponse {
    
    @JsonProperty("code")
    private Integer code;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("fileIndex")
    private String fileIndex;
    
    @JsonProperty("id")
    private String id;
    
    public LargeFileUploadResponse() {
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
    
    public String getFileIndex() {
        return fileIndex;
    }
    
    public void setFileIndex(String fileIndex) {
        this.fileIndex = fileIndex;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    /**
     * 判断请求是否成功
     */
    public boolean isSuccess() {
        return code != null && code == 200;
    }
    
    /**
     * 获取当前上传的文件位置索引（字节
     */
    public Long getFileIndexAsLong() {
        try {
            return fileIndex != null ? Long.parseLong(fileIndex) : 0L;
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
    
    /**
     * 判断是否上传完成
     */
    public boolean isUploadComplete() {
        return isSuccess() && id != null && !id.trim().isEmpty();
    }
    
    @Override
    public String toString() {
        return "LargeFileUploadResponse{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", fileIndex='" + fileIndex + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
