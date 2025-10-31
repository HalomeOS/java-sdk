package com.guangyuyun.sdk.example;

import com.guangyuyun.sdk.GuangYuYunClient;
import com.guangyuyun.sdk.exception.GuangYuYunException;
import com.guangyuyun.sdk.model.FileDownloadResponse;
import com.guangyuyun.sdk.model.TokenResponse;
import com.guangyuyun.sdk.util.DownloadUtils;

import java.io.File;
import java.io.IOException;

/**
 * 文件下载使用示例
 */
public class FileDownloadExample {
    
    public static void main(String[] args) {
        // 示例1: 基本文件下载
        basicFileDownload();
    }
    
    /**
     * 基本文件下载示例
     */
    public static void basicFileDownload() {
        System.out.println("=== 基本文件下载示例 ===");
        
        GuangYuYunClient client = new GuangYuYunClient();
        
        try {
            String fileId = "QmPiLmyx6xVqZ3xJGY6hecUrudPVAUBnUTLypevHSzeZej";
            String authToken = "0446bdae5002deb7653aa54c9bc7b9f4";
            String outputPath = "music.zip";
            
            FileDownloadResponse response = client.downloadFile(fileId, authToken, outputPath);
            
            if (response.isSuccess()) {
                System.out.println("✓ 文件下载成功!");
                System.out.println("文件路径: " + response.getFilePath());
                System.out.println("文件大小: " + DownloadUtils.formatFileSize(response.getTotalSize()));
                System.out.println("下载大小: " + DownloadUtils.formatFileSize(response.getDownloadedSize()));
                System.out.println("是否完成: " + response.isCompleted());
            } else {
                System.out.println("✗ 文件下载失败: " + response.getMessage());
            }
            
        } catch (GuangYuYunException e) {
            System.err.println("✗ 下载错误: " + e.getMessage());
        } finally {
            client.close();
        }
        
        System.out.println();
    }

}