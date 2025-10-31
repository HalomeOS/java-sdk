package com.guangyuyun.sdk.model;

/**
 * 文件下载请求参数
 */
public class FileDownloadRequest {
    
    private String fileId;
    private String authToken;
    private String outputPath;
    private Long startByte;
    private Long endByte;
    private Integer chunkSize;
    private boolean resumeEnabled;
    
    public FileDownloadRequest() {
        this.resumeEnabled = true;
    }
    
    public FileDownloadRequest(String fileId, String authToken, String outputPath) {
        this.fileId = fileId;
        this.authToken = authToken;
        this.outputPath = outputPath;
        this.resumeEnabled = true;
    }
    
    public String getFileId() {
        return fileId;
    }
    
    public void setFileId(String fileId) {
        this.fileId = fileId;
    }
    
    public String getAuthToken() {
        return authToken;
    }
    
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
    
    public String getOutputPath() {
        return outputPath;
    }
    
    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }
    
    public Long getStartByte() {
        return startByte;
    }
    
    public void setStartByte(Long startByte) {
        this.startByte = startByte;
    }
    
    public Long getEndByte() {
        return endByte;
    }
    
    public void setEndByte(Long endByte) {
        this.endByte = endByte;
    }
    
    public Integer getChunkSize() {
        return chunkSize;
    }
    
    public void setChunkSize(Integer chunkSize) {
        this.chunkSize = chunkSize;
    }
    
    public boolean isResumeEnabled() {
        return resumeEnabled;
    }
    
    public void setResumeEnabled(boolean resumeEnabled) {
        this.resumeEnabled = resumeEnabled;
    }
    
    @Override
    public String toString() {
        return "FileDownloadRequest{" +
                "fileId='" + fileId + '\'' +
                ", authToken='" + authToken + '\'' +
                ", outputPath='" + outputPath + '\'' +
                ", startByte=" + startByte +
                ", endByte=" + endByte +
                ", chunkSize=" + chunkSize +
                ", resumeEnabled=" + resumeEnabled +
                '}';
    }
}