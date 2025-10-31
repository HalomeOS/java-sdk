package com.guangyuyun.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Token响应结果
 */
public class TokenResponse {
    
    @JsonProperty("code")
    private Integer code;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("data")
    private Object data;
    
    public TokenResponse() {
    }
    
    public Integer getCode() {
        return code;
    }
    
    public void setCode(Integer code) {
        this.code = code;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public Object getData() {
        return data;
    }
    
    public void setData(Object data) {
        this.data = data;
    }
    
    /**
     * 获取Token字符串（兼容字符串格式的响应）
     */
    public String getToken() {
        if (data instanceof String) {
            return (String) data;
        } else if (data instanceof TokenData) {
            return ((TokenData) data).getToken();
        }
        return null;
    }
    
    /**
     * 获取过期时间（兼容TokenData格式的响应）
     */
    public Long getExpireTime() {
        if (data instanceof TokenData) {
            return ((TokenData) data).getExpireTime();
        }
        return null;
    }
    
    /**
     * 判断请求是否成功
     */
    public boolean isSuccess() {
        return code != null && code == 200;
    }
    
    @Override
    public String toString() {
        return "TokenResponse{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
    
    /**
     * Token数据
     */
    public static class TokenData {
        
        @JsonProperty("token")
        private String token;
        
        @JsonProperty("expireTime")
        private Long expireTime;
        
        public TokenData() {
        }
        
        public String getToken() {
            return token;
        }
        
        public void setToken(String token) {
            this.token = token;
        }
        
        public Long getExpireTime() {
            return expireTime;
        }
        
        public void setExpireTime(Long expireTime) {
            this.expireTime = expireTime;
        }
        
        @Override
        public String toString() {
            return "TokenData{" +
                    "token='" + token + '\'' +
                    ", expireTime=" + expireTime +
                    '}';
        }
    }
}
