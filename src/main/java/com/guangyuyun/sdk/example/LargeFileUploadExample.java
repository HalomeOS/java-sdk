package com.guangyuyun.sdk.example;

import com.guangyuyun.sdk.GuangYuYunClient;
import com.guangyuyun.sdk.exception.GuangYuYunException;
import com.guangyuyun.sdk.model.LargeFileUploadRequest;
import com.guangyuyun.sdk.model.LargeFileUploadResponse;
import com.guangyuyun.sdk.model.TokenResponse;
import com.guangyuyun.sdk.util.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * 大文件上传使用示例
 */
public class LargeFileUploadExample {
    
    public static void main(String[] args) {
        // 示例1: 基本大文件上传
        basicLargeFileUpload();
    }
    
    /**
     * 基本大文件上传示例（根据文件路径）
     */
    public static void basicLargeFileUpload() {
        System.out.println("=== 基本大文件上传示例（根据文件路径）===");
        
        GuangYuYunClient client = new GuangYuYunClient();
        
        try {
            // 指定要上传的文件路径
            // File file=createFile("test_large_file.txt", 30*1024 * 1024);
            String filePath = "E:\\gxjh.sql"; // 请修改为实际的文件路径
            
            File file = new File(filePath);
            
            // 检查文件是否存在
            if (!file.exists()) {
                System.err.println("文件不存在: " + filePath);
                System.out.println("请修改filePath变量为实际存在的文件路径");
                return;
            }
            
            System.out.println("准备上传文件: " + filePath);
            System.out.println("文件大小: " + file.length() + " bytes");
            
            String authToken = "ccc74a42911975a6ecee9df68e0ea37a";
            
            LargeFileUploadResponse response = client.uploadLargeFile(file, authToken);
            System.out.println("返回值: " + response);
            
            if (response.isSuccess()) {
                System.out.println("文件上传成功!");
                System.out.println("文件ID: " + response.getId());
                System.out.println("当前索引: " + response.getFileIndex());
                System.out.println("是否完成: " + response.isUploadComplete());
            } else {
                System.out.println("文件上传失败: " + response.getMessage());
                System.out.println("错误码: " + response.getCode());
            }
            
            // 文件上传完成，无需清理
            
        } catch (GuangYuYunException e) {
            System.err.println("上传错误: " + e.getMessage());
        } finally {
            client.close();
        }
        
        System.out.println();
    }

    
    /**
     * 创建测试文件
     */
    private static File createFile(String fileName, int size) throws IOException {
        File file = File.createTempFile(fileName.replace(".txt", ""), ".txt");
        file.deleteOnExit();
        
        // 写入测试数据
        StringBuilder content = new StringBuilder();
        String testData = "This is test data for large file upload. ";
        int repeatCount = size / testData.length() + 1;
        
        for (int i = 0; i < repeatCount; i++) {
            content.append(testData);
        }
        
        java.nio.file.Files.write(file.toPath(), content.toString().getBytes());
        
        return file;
    }
}
