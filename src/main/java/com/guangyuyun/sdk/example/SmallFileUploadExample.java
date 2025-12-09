package com.guangyuyun.sdk.example;

import com.guangyuyun.sdk.GuangYuYunClient;
import com.guangyuyun.sdk.exception.GuangYuYunException;
import com.guangyuyun.sdk.model.SmallFileUploadResponse;

import java.io.File;
import java.io.IOException;

/**
 * 小文件上传使用示例
 */
public class SmallFileUploadExample {
    
    public static void main(String[] args) {
        // 示例1: 基本小文件上传
        basicSmallFileUpload();
    }
    
    /**
     * 基本小文件上传示例（根据文件路径）
     */
    public static void basicSmallFileUpload() {
        System.out.println("=== 基本小文件上传示例（根据文件路径）===");
        
        GuangYuYunClient client = new GuangYuYunClient();
        
        try {
            // 指定要上传的文件路径
            String filePath = "test_small_file.txt"; // 请修改为实际的文件路径
            
            // 如果文件不存在，创建一个测试文件
            File file = new File(filePath);
            if (!file.exists()) {
                System.out.println("文件不存在，创建测试文件: " + filePath);
                file = createTestFile(filePath, 1024); // 创建1KB的测试文件
            }
            
            // 检查文件是否存在
            if (!file.exists()) {
                System.err.println("文件不存在: " + filePath);
                System.out.println("请修改filePath变量为实际存在的文件路径");
                return;
            }
            
            System.out.println("准备上传文件: " + file.getAbsolutePath());
            System.out.println("文件大小: " + file.length() + " bytes (" + formatFileSize(file.length()) + ")");
            
            String authToken = "93d692153446347792d62113209288bf"; // 请修改为实际的token
            
            SmallFileUploadResponse response = client.uploadSmallFile(file, authToken);
            System.out.println("返回值: " + response);
            
            if (response.isSuccess()) {
                System.out.println("✓ 文件上传成功!");
                System.out.println("文件ID: " + response.getFileId());
                System.out.println("返回消息: " + response.getMessage());
            } else {
                System.out.println("✗ 文件上传失败: " + response.getMessage());
                System.out.println("错误码: " + response.getCode());
            }
            
        } catch (GuangYuYunException e) {
            System.err.println("✗ 上传错误: " + e.getMessage());
            if (e.getCode() != null) {
                System.err.println("错误码: " + e.getCode());
            }
        } catch (IOException e) {
            System.err.println("✗ 文件操作错误: " + e.getMessage());
        } finally {
            client.close();
        }
        
        System.out.println();
    }

    /**
     * 创建测试文件
     */
    private static File createTestFile(String fileName, int size) throws IOException {
        File file = new File(fileName);
        
        // 写入测试数据
        StringBuilder content = new StringBuilder();
        String testData = "This is test data for small file upload. ";
        int repeatCount = size / testData.length() + 1;
        
        for (int i = 0; i < repeatCount; i++) {
            content.append(testData);
        }
        
        // 截取到指定大小
        String finalContent = content.toString().substring(0, Math.min(size, content.length()));
        java.nio.file.Files.write(file.toPath(), finalContent.getBytes());
        
        return file;
    }
    
    /**
     * 格式化文件大小
     */
    private static String formatFileSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else {
            return String.format("%.2f MB", size / (1024.0 * 1024.0));
        }
    }
}

