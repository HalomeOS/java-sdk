package com.guangyuyun.sdk.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 文件工具类
 */
public class FileUtils {
    
    private static final int DEFAULT_CHUNK_SIZE = 5 * 1024 * 1024; // 5MB
    private static final int MIN_CHUNK_SIZE = 1024 * 1024; // 1MB
    private static final int MAX_CHUNK_SIZE = 10 * 1024 * 1024; // 10MB
    
    /**
     * 计算文件的MD5值
     */
    public static String calculateMD5(File file) throws IOException {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IOException("MD5 algorithm not available", e);
        }
        
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                md.update(buffer, 0, bytesRead);
            }
        }
        
        byte[] digest = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
    
    
    
    /**
     * 获取最小分片大小
     */
    public static int getMinChunkSize() {
        return MIN_CHUNK_SIZE;
    }
    
    /**
     * 验证分片大小是否在有效范围内
     */
    public static boolean isValidChunkSize(int chunkSize) {
        return chunkSize >= MIN_CHUNK_SIZE && chunkSize <= MAX_CHUNK_SIZE;
    }
    
    
    /**
     * 读取文件分片
     */
    public static byte[] readFileChunk(File file, long startIndex, int chunkSize) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            fis.skip(startIndex);
            byte[] buffer = new byte[chunkSize];
            int bytesRead = fis.read(buffer);
            
            if (bytesRead == -1) {
                return new byte[0];
            }
            
            if (bytesRead < chunkSize) {
                byte[] result = new byte[bytesRead];
                System.arraycopy(buffer, 0, result, 0, bytesRead);
                return result;
            }
            
            return buffer;
        }
    }
}
