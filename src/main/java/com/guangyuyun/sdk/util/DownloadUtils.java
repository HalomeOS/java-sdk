package com.guangyuyun.sdk.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * 下载工具类
 */
public class DownloadUtils {
    
    private static final int DEFAULT_CHUNK_SIZE = 1024 * 1024; // 1MB
    private static final int MIN_CHUNK_SIZE = 64 * 1024; // 64KB
    private static final int MAX_CHUNK_SIZE = 10 * 1024 * 1024; // 10MB
    
    /**
     * 获取默认分片大小
     */
    public static int getDefaultChunkSize() {
        return DEFAULT_CHUNK_SIZE;
    }
    
    /**
     * 验证分片大小是否在有效范围内
     */
    public static boolean isValidChunkSize(int chunkSize) {
        return chunkSize >= MIN_CHUNK_SIZE && chunkSize <= MAX_CHUNK_SIZE;
    }
    
    /**
     * 检查文件是否存在
     */
    public static boolean fileExists(String filePath) {
        return Files.exists(Paths.get(filePath));
    }
    
    /**
     * 获取文件大小
     */
    public static long getFileSize(String filePath) {
        try {
            return Files.size(Paths.get(filePath));
        } catch (IOException e) {
            return 0;
        }
    }
    
    /**
     * 创建目录（如果不存在）
     */
    public static void createDirectories(String filePath) throws IOException {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IOException("File path cannot be null or empty");
        }
        
        try {
            Path path = Paths.get(filePath);
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
        } catch (IOException e) {
            throw new IOException("Failed to create directories for path: " + filePath + ", Error: " + e.getMessage(), e);
        }
    }
    
    
    /**
     * 将字节数组追加到文件
     */
    public static void appendToFile(String filePath, byte[] data) throws IOException {
        if (data == null || data.length == 0) {
            return; // 空数据不需要写入
        }
        
        try {
            Path path = Paths.get(filePath);
            Files.write(path, data, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new IOException("Failed to append data to file: " + filePath + ", Error: " + e.getMessage(), e);
        }
    }
    
    /**
     * 将字节数组写入文件（覆盖模式）
     */
    public static void writeToFile(String filePath, byte[] data) throws IOException {
        if (data == null || data.length == 0) {
            // 创建空文件
            data = new byte[0];
        }
        
        try {
            Path path = Paths.get(filePath);
            Files.write(path, data, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new IOException("Failed to write data to file: " + filePath + ", Error: " + e.getMessage(), e);
        }
    }
    
    
    
    
    
    /**
     * 格式化文件大小
     */
    public static String formatFileSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.2f KB", bytes / 1024.0);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
        } else {
            return String.format("%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0));
        }
    }
    
    /**
     * 格式化下载速度
     */
    public static String formatSpeed(long bytesPerSecond) {
        return formatFileSize(bytesPerSecond) + "/s";
    }
    
}
