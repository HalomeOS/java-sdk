package com.guangyuyun.sdk.example;

import com.guangyuyun.sdk.GuangYuYunClient;
import com.guangyuyun.sdk.exception.GuangYuYunException;
import com.guangyuyun.sdk.model.TokenResponse;

/**
 * GuangYuYun SDK使用示例
 */
public class TokenExample {
    
    public static void main(String[] args) {
        // 示例1: 基本用法
        tokenExample();
    }
    
    /**
     * 基本用法示例
     */
    public static void tokenExample() {
        System.out.println("=== 基本用法示例 ===");
        
        GuangYuYunClient client = new GuangYuYunClient();
        
        try {
            String account = "1753753200";
            String apiKey = "d926e23586984e3b8b52c3911427ff15";
            Long expireTime = System.currentTimeMillis() / 1000 + 7200; // 10位时间戳，2小时后过期
            
            TokenResponse response = client.createToken(account, apiKey, expireTime);
            
            if (response.isSuccess()) {
                System.out.println("✓ Token创建成功!");
                System.out.println("Token: " + response.getToken());
                System.out.println("过期时间: " + expireTime);
            } else {
                System.out.println("✗ Token创建失败: " + response.getMessage());
            }
            
        } catch (GuangYuYunException e) {
            System.err.println("✗ SDK错误: " + e.getMessage());
        } finally {
            client.close();
        }
        
        System.out.println();
    }
}
