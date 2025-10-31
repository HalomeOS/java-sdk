package com.guangyuyun.sdk.model;

/**
 * 文件下载响应结果
 */
public class FileDownloadResponse {
    
    private boolean success;
    private String message;
    private String filePath;
    private Long totalSize;
    private Long downloadedSize;
    private String fileId;
    private boolean completed;
    private Integer statusCode;
    
    public FileDownloadResponse() {
    }
    
    public FileDownloadResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getFilePath() {
        return filePath;
    }
    
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    
    public Long getTotalSize() {
        return totalSize;
    }
    
    public void setTotalSize(Long totalSize) {
        this.totalSize = totalSize;
    }
    
    public Long getDownloadedSize() {
        return downloadedSize;
    }
    
    public void setDownloadedSize(Long downloadedSize) {
        this.downloadedSize = downloadedSize;
    }
    
    public String getFileId() {
        return fileId;
    }
    
    public void setFileId(String fileId) {
        this.fileId = fileId;
    }
    
    public boolean isCompleted() {
        return completed;
    }
    
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
    
    public Integer getStatusCode() {
        return statusCode;
    }
    
    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }
    
    /**
     * 获取下载进度百分比
     */
    public double getProgressPercentage() {
        if (totalSize == null || totalSize <= 0) {
            return 0.0;
        }
        if (downloadedSize == null) {
            return 0.0;
        }
        return (double) downloadedSize / totalSize * 100.0;
    }
    
    /**
     * 获取剩余字节数
     */
    public Long getRemainingBytes() {
        if (totalSize == null || downloadedSize == null) {
            return null;
        }
        return Math.max(0, totalSize - downloadedSize);
    }
    
    @Override
    public String toString() {
        return "FileDownloadResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", filePath='" + filePath + '\'' +
                ", totalSize=" + totalSize +
                ", downloadedSize=" + downloadedSize +
                ", fileId='" + fileId + '\'' +
                ", completed=" + completed +
                ", statusCode=" + statusCode +
                '}';
    }
}
