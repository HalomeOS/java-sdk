package com.guangyuyun.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Token请求参数
 */
public class TokenRequest {
    
    @JsonProperty("account")
    private String account;
    
    @JsonProperty("apiKey")
    private String apiKey;
    
    @JsonProperty("expireTime")
    private Long expireTime;
    
    public TokenRequest() {
    }
    
    public TokenRequest(String account, String apiKey, Long expireTime) {
        this.account = account;
        this.apiKey = apiKey;
        this.expireTime = expireTime;
    }
    
    public String getAccount() {
        return account;
    }
    
    public void setAccount(String account) {
        this.account = account;
    }
    
    public String getApiKey() {
        return apiKey;
    }
    
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
    
    public Long getExpireTime() {
        return expireTime;
    }
    
    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }
    
    @Override
    public String toString() {
        return "TokenRequest{" +
                "account='" + account + '\'' +
                ", apiKey='" + apiKey + '\'' +
                ", expireTime=" + expireTime +
                '}';
    }
}
