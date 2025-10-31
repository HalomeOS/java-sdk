package com.guangyuyun.sdk.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 大文件上传请求参数
 */
public class LargeFileUploadRequest {
    
    @JsonIgnore
    private String fileName;
    
    @JsonIgnore
    private String fileMd5;
    
    @JsonIgnore
    private Long fileSize;
    
    @JsonIgnore
    private Long fileStartIndex;
    
    @JsonIgnore
    private String authToken;
    
    @JsonIgnore
    private String notificationLink;
    
    @JsonIgnore
    private byte[] fileData;
    
    public LargeFileUploadRequest() {
    }
    
    public LargeFileUploadRequest(String fileName, String fileMd5, Long fileSize, 
                                Long fileStartIndex, String authToken, byte[] fileData) {
        this.fileName = fileName;
        this.fileMd5 = fileMd5;
        this.fileSize = fileSize;
        this.fileStartIndex = fileStartIndex;
        this.authToken = authToken;
        this.fileData = fileData;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public String getFileMd5() {
        return fileMd5;
    }
    
    public void setFileMd5(String fileMd5) {
        this.fileMd5 = fileMd5;
    }
    
    public Long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
    
    public Long getFileStartIndex() {
        return fileStartIndex;
    }
    
    public void setFileStartIndex(Long fileStartIndex) {
        this.fileStartIndex = fileStartIndex;
    }
    
    public String getAuthToken() {
        return authToken;
    }
    
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
    
    public String getNotificationLink() {
        return notificationLink;
    }
    
    public void setNotificationLink(String notificationLink) {
        this.notificationLink = notificationLink;
    }
    
    public byte[] getFileData() {
        return fileData;
    }
    
    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }
    
    @Override
    public String toString() {
        return "LargeFileUploadRequest{" +
                "fileName='" + fileName + '\'' +
                ", fileMd5='" + fileMd5 + '\'' +
                ", fileSize=" + fileSize +
                ", fileStartIndex=" + fileStartIndex +
                ", authToken='" + authToken + '\'' +
                ", notificationLink='" + notificationLink + '\'' +
                ", fileDataSize=" + (fileData != null ? fileData.length : 0) +
                '}';
    }
}
