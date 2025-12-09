package com.guangyuyun.sdk.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.File;

/**
 * 小文件上传请求参数
 */
public class SmallFileUploadRequest {
    
    @JsonIgnore
    private File file;
    
    @JsonIgnore
    private String authToken;
    
    public SmallFileUploadRequest() {
    }
    
    public SmallFileUploadRequest(File file, String authToken) {
        this.file = file;
        this.authToken = authToken;
    }
    
    public File getFile() {
        return file;
    }
    
    public void setFile(File file) {
        this.file = file;
    }
    
    public String getAuthToken() {
        return authToken;
    }
    
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
    
    @Override
    public String toString() {
        return "SmallFileUploadRequest{" +
                "file=" + (file != null ? file.getName() : "null") +
                ", fileSize=" + (file != null ? file.length() : 0) +
                ", authToken='" + authToken + '\'' +
                '}';
    }
}

