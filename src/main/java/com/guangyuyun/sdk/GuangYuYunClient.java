package com.guangyuyun.sdk;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guangyuyun.sdk.exception.GuangYuYunException;
import com.guangyuyun.sdk.model.FileDownloadRequest;
import com.guangyuyun.sdk.model.FileDownloadResponse;
import com.guangyuyun.sdk.model.LargeFileUploadRequest;
import com.guangyuyun.sdk.model.LargeFileUploadResponse;
import com.guangyuyun.sdk.model.SmallFileUploadRequest;
import com.guangyuyun.sdk.model.SmallFileUploadResponse;
import com.guangyuyun.sdk.model.TokenRequest;
import com.guangyuyun.sdk.model.TokenResponse;
import com.guangyuyun.sdk.util.DownloadUtils;
import com.guangyuyun.sdk.util.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * GuangYuYun API客户端
 */
public class GuangYuYunClient {
    
    private static final Logger logger = LoggerFactory.getLogger(GuangYuYunClient.class);
    
    private static final String DEFAULT_BASE_URL = "https://gw.halome.cc";
    private static final String CREATE_TOKEN_ENDPOINT = "/u/createToken";//创建Token
    private static final String LARGE_FILE_UPLOAD_ENDPOINT = "/v1/addLargeFile";//大文件上传
    private static final String SMALL_FILE_UPLOAD_ENDPOINT = "/v1/add";//小文件上传
    private static final String FILE_DOWNLOAD_ENDPOINT = "/v1/cat";//下载
    
    private final String baseUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private String authToken; // 统一的认证token
    
    /**
     * 使用默认配置创建客户端
     */
    public GuangYuYunClient() {
        this(DEFAULT_BASE_URL);
    }
    
    /**
     * 使用自定义基础URL创建客户端
     */
    public GuangYuYunClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.httpClient = HttpClients.createDefault();
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * 设置认证Token
     * 
     * @param authToken 认证token
     */
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
        logger.info("Auth token has been set");
    }
    
    /**
     * 获取当前认证Token
     * 
     * @return 当前认证token
     */
    public String getAuthToken() {
        return this.authToken;
    }
    
    /**
     * 检查是否已设置认证Token
     * 
     * @return 是否已设置token
     */
    public boolean hasAuthToken() {
        return this.authToken != null && !this.authToken.trim().isEmpty();
    }
    
    /**
     * 清除认证Token
     */
    public void clearAuthToken() {
        this.authToken = null;
        logger.info("Auth token has been cleared");
    }
    
    /**
     * 创建Token
     * 
     * @param account 账号
     * @param apiKey API密钥
     * @param expireTime 到期时间戳
     * @return Token响应结果
     * @throws GuangYuYunException 当请求失败时抛出异常
     */
    public TokenResponse createToken(String account, String apiKey, Long expireTime) throws GuangYuYunException {
        TokenRequest request = new TokenRequest(account, apiKey, expireTime);
        return createToken(request);
    }
    
    /**
     * 创建Token
     * 
     * @param request Token请求参数
     * @return Token响应结果
     * @throws GuangYuYunException 当请求失败时抛出异常
     */
    public TokenResponse createToken(TokenRequest request) throws GuangYuYunException {
        validateRequest(request);
        
        try {
            String url = baseUrl + CREATE_TOKEN_ENDPOINT;
            String jsonRequest = objectMapper.writeValueAsString(request);
            
            logger.debug("Creating token request to: {}", url);
            logger.debug("Request body: {}", jsonRequest);
            
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-Type", "application/json; charset=UTF-8");
            httpPost.setEntity(new StringEntity(jsonRequest, StandardCharsets.UTF_8));
            
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            
            if (entity != null) {
                String responseBody = EntityUtils.toString(entity, StandardCharsets.UTF_8);
                logger.debug("Response body: {}", responseBody);
                
                TokenResponse tokenResponse = objectMapper.readValue(responseBody, TokenResponse.class);
                
                if (!tokenResponse.isSuccess()) {
                    throw new GuangYuYunException(tokenResponse.getCode(), 
                        "Failed to create token: " + tokenResponse.getMessage(),tokenResponse.getCode());
                }
                
                return tokenResponse;
            } else {
                throw new GuangYuYunException("Empty response from server");
            }
            
        } catch (IOException e) {
            logger.error("Error creating token", e);
            throw new GuangYuYunException("Network error: " + e.getMessage(), e);
        }
    }
    
    
     /**
     * 上传小文件
     * 
     * @param file 要上传的文件
     * @param authToken 认证令牌
     * @return 上传结果
     * @throws GuangYuYunException 当请求失败时抛出异常
     */
    public SmallFileUploadResponse uploadSmallFile(File file, String authToken) throws GuangYuYunException {
        SmallFileUploadRequest request = new SmallFileUploadRequest(file, authToken);
        return uploadSmallFile(request);
    }
    
    /**
     * 上传小文件（使用统一token）
     * 
     * @param file 要上传的文件
     * @return 上传结果
     * @throws GuangYuYunException 当请求失败时抛出异常
     */
    public SmallFileUploadResponse uploadSmallFile(File file) throws GuangYuYunException {
        if (!hasAuthToken()) {
            throw new GuangYuYunException("AuthToken not set. Please call setAuthToken() first or use uploadSmallFile(file, authToken)");
        }
        return uploadSmallFile(file, this.authToken);
    }
    
    /**
     * 上传小文件
     * 
     * @param request 小文件上传请求参数
     * @return 上传结果
     * @throws GuangYuYunException 当请求失败时抛出异常
     */
    public SmallFileUploadResponse uploadSmallFile(SmallFileUploadRequest request) throws GuangYuYunException {
        validateSmallFileUploadRequest(request);
        
        try {
            String url = baseUrl + SMALL_FILE_UPLOAD_ENDPOINT;
            
            logger.debug("Uploading small file to: {}", url);
            logger.debug("Request: {}", request);
            
            // 检查文件大小（不能超过100MB）
            long fileSize = request.getFile().length();
            long maxSize = 100 * 1024 * 1024; // 100MB
            if (fileSize > maxSize) {
                throw new GuangYuYunException("File size exceeds 100MB. Please use large file upload interface for files larger than 100MB");
            }
            
            HttpPost httpPost = new HttpPost(url);
            
            // 设置请求头
            httpPost.setHeader("AuthToken", request.getAuthToken());
            
            // 创建multipart/form-data请求体
            // SDK自动判断使用FileBody还是InputStreamBody
            // 优先使用FileBody（性能更好），如果文件不可读或需要流式处理则使用InputStreamBody
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            
            boolean useFileBody = shouldUseFileBody(request.getFile());
            
            if (useFileBody) {
                // 使用FileBody方式（推荐，性能更好，直接使用文件）
                FileBody fileBody = new FileBody(
                    request.getFile(),
                    ContentType.DEFAULT_BINARY,
                    request.getFile().getName()
                );
                builder.addPart("file", fileBody);
                logger.debug("Auto-selected: Using FileBody with file: {}", request.getFile().getName());
            } else {
                // 使用InputStreamBody方式（从文件创建输入流）
                FileInputStream fileInputStream = new FileInputStream(request.getFile());
                InputStreamBody inputStreamBody = new InputStreamBody(
                    fileInputStream,
                    ContentType.DEFAULT_BINARY,
                    request.getFile().getName()
                );
                builder.addPart("file", inputStreamBody);
                logger.debug("Auto-selected: Using InputStreamBody with file: {}", request.getFile().getName());
            }
            
            // MultipartEntityBuilder会自动设置Content-Type: multipart/form-data; boundary=...
            HttpEntity multipartEntity = builder.build();
            httpPost.setEntity(multipartEntity);
            
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            
            if (entity != null) {
                String responseBody = EntityUtils.toString(entity, StandardCharsets.UTF_8);
                logger.debug("Response body: {}", responseBody);
                
                SmallFileUploadResponse uploadResponse = objectMapper.readValue(responseBody, SmallFileUploadResponse.class);
                
                if (!uploadResponse.isSuccess()) {
                    String errorMessage = getErrorMessage(uploadResponse.getCode(), uploadResponse.getMessage());
                    throw new GuangYuYunException(uploadResponse.getCode(), 
                        "Failed to upload file: " + errorMessage, uploadResponse.getCode());
                }
                
                return uploadResponse;
            } else {
                throw new GuangYuYunException("Empty response from server");
            }
            
        } catch (IOException e) {
            logger.error("Error uploading small file", e);
            throw new GuangYuYunException("Network error: " + e.getMessage(), e);
        }
    }
    
    /**
     * 根据错误码获取错误消息
     */
    private String getErrorMessage(Integer code, String defaultMessage) {
        if (code == null) {
            return defaultMessage != null ? defaultMessage : "Unknown error";
        }
        
        switch (code) {
            case 4:
                return "不支持多文件上传";
            case 5:
                return "单文件大小不能超过100M,大文件上传请调用大文件上传接口";
            case 6:
                return "文件错误";
            case 7:
                return "添加到分布式存储系统错误";
            case 8:
                return "写入数据库错误";
            default:
                return defaultMessage != null ? defaultMessage : "Unknown error code: " + code;
        }
    }
    
    
    
    /**
     * 上传大文件分片
     * 
     * @param request 大文件上传请求参数
     * @return 大文件上传响应结果
     * @throws GuangYuYunException 当请求失败时抛出异常
     */
    public LargeFileUploadResponse uploadLargeFileChunk(LargeFileUploadRequest request) throws GuangYuYunException {
        validateLargeFileUploadRequest(request);
        
        try {
            String url = baseUrl + LARGE_FILE_UPLOAD_ENDPOINT;
            
            logger.debug("Uploading large file chunk to: {}", url);
            logger.debug("Request: {}", request);
            
            HttpPost httpPost = new HttpPost(url);
            
            // 设置请求头
            httpPost.setHeader("FileStartIndex", String.valueOf(request.getFileStartIndex()));
            httpPost.setHeader("FileSize", String.valueOf(request.getFileSize()));
            httpPost.setHeader("FileName", request.getFileName());
            httpPost.setHeader("FileMd5", request.getFileMd5());
            httpPost.setHeader("AuthToken", request.getAuthToken());
            
            if (request.getNotificationLink() != null && !request.getNotificationLink().trim().isEmpty()) {
                httpPost.setHeader("NotificationLink", request.getNotificationLink());
            }
            
            // 设置文件数据
            httpPost.setEntity(new ByteArrayEntity(request.getFileData()));
            
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            
            if (entity != null) {
                String responseBody = EntityUtils.toString(entity, StandardCharsets.UTF_8);
                logger.debug("Response body: {}", responseBody);
                
                LargeFileUploadResponse uploadResponse = objectMapper.readValue(responseBody, LargeFileUploadResponse.class);
                
                // 对于错误码7（文件索引错误），不抛出异常，直接返回响应对象
                // 让调用方处理这种情况
                if (!uploadResponse.isSuccess() && uploadResponse.getCode() != 7) {
                    throw new GuangYuYunException(uploadResponse.getCode(), 
                        "Failed to upload file chunk: " + uploadResponse.getMessage(),uploadResponse.getCode());
                }
                
                return uploadResponse;
            } else {
                throw new GuangYuYunException("Empty response from server");
            }
            
        } catch (IOException e) {
            logger.error("Error uploading file chunk", e);
            throw new GuangYuYunException("Network error: " + e.getMessage(), e);
        }
    }
    
    /**
     * 上传大文件（支持断点续传）
     * 
     * @param file 要上传的文件
     * @param authToken 认证令牌
     * @param chunkSize 分片大小（字节），可选范围1MB-10MB
     * @param notificationLink 异步通知链接（可选）
     * @return 上传结果
     * @throws GuangYuYunException 当请求失败时抛出异常
     */
    public LargeFileUploadResponse uploadLargeFile(File file, String authToken, 
                                                 Integer chunkSize, String notificationLink) throws GuangYuYunException {
        if (file == null || !file.exists()) {
            throw new GuangYuYunException("File does not exist");
        }
        
        if (authToken == null || authToken.trim().isEmpty()) {
            throw new GuangYuYunException("AuthToken cannot be null or empty");
        }
        
        // 使用默认分片大小
        if (chunkSize == null) {
            chunkSize = FileUtils.getMinChunkSize();
        }
        
        // 验证分片大小
        if (!FileUtils.isValidChunkSize(chunkSize)) {
            throw new GuangYuYunException("Chunk size must be between 1MB and 10MB");
        }
        
        try {
            // 在上传前尽量调整分片大小，避免最后一片小于1MB而被服务端拒绝
            long fileSizeForAdjust = file.length();
            int minChunk = FileUtils.getMinChunkSize();
            if (fileSizeForAdjust > 0) {
                long remainder = fileSizeForAdjust % chunkSize;
                if (remainder > 0 && remainder < minChunk) {
                    int step = 64 * 1024; // 64KB步进调整，兼顾效率与精度
                    int original = chunkSize;
                    boolean adjusted = false;
                    // 优先尝试减小分片大小
                    for (int candidate = chunkSize - step; candidate >= minChunk; candidate -= step) {
                        if (!FileUtils.isValidChunkSize(candidate)) continue;
                        long r = fileSizeForAdjust % candidate;
                        if (r == 0 || r >= minChunk) {
                            chunkSize = candidate;
                            adjusted = true;
                            break;
                        }
                    }
                    // 再尝试增大分片大小
                    if (!adjusted) {
                        for (int candidate = chunkSize + step; FileUtils.isValidChunkSize(candidate); candidate += step) {
                            long r = fileSizeForAdjust % candidate;
                            if (r == 0 || r >= minChunk) {
                                chunkSize = candidate;
                                adjusted = true;
                                break;
                            }
                        }
                    }
                    if (adjusted) {
                        System.out.println("自动调整分片大小以保证最后一片≥1MB: " + original + " -> " + chunkSize + " bytes (" + String.format("%.2f", chunkSize / (1024.0 * 1024.0)) + " MB)");
                    } else {
                        System.out.println("未能在1MB-10MB范围内找到可避免最后一片<1MB的分片大小，可能导致最后一片被拒绝。建议手动调整分片大小。");
                    }
                }
            }

            String fileName = file.getName();
            String fileMd5 = FileUtils.calculateMD5(file);
            long fileSize = file.length();
            
            logger.info("Starting large file upload: {} ({} bytes)", fileName, fileSize);
            System.out.println("上传参数 -> 文件名: " + fileName + ", 文件MD5: " + fileMd5 + ", 文件大小: " + fileSize + " bytes (" + com.guangyuyun.sdk.util.DownloadUtils.formatFileSize(fileSize) + ")");
            System.out.println("上传参数 -> 分片大小: " + chunkSize + " bytes (" + String.format("%.2f", chunkSize / (1024.0 * 1024.0)) + " MB)");
            
            long currentIndex = 0;
            long chunkNumber = 1;
            LargeFileUploadResponse lastResponse = null;
            
            while (currentIndex < fileSize) {
                // 计算当前分片大小
                int currentChunkSize = (int) Math.min(chunkSize, fileSize - currentIndex);
                
                // 打印分片日志（大小与起始索引）
                System.out.println("=== 上传分片 " + chunkNumber + " ===");
                System.out.println("起始索引: " + currentIndex + " bytes");
                System.out.println("计划上传分片大小: " + currentChunkSize + " bytes (" + String.format("%.2f", currentChunkSize / (1024.0 * 1024.0)) + " MB)");
                if (currentChunkSize < 1024 * 1024) {
                    System.out.println("[警告] 当前分片小于1MB，可能被服务端拒绝。建议调整分片大小避免最后一片 < 1MB。");
                } else if (currentChunkSize > 50 * 1024 * 1024) {
                    System.out.println("[警告] 当前分片大于50MB，服务端不允许。");
                }

                // 读取文件分片
                byte[] chunkData = FileUtils.readFileChunk(file, currentIndex, currentChunkSize);
                
                // 创建上传请求
                LargeFileUploadRequest request = new LargeFileUploadRequest();
                request.setFileName(fileName);
                request.setFileMd5(fileMd5);
                request.setFileSize(fileSize);
                request.setFileStartIndex(currentIndex);
                request.setAuthToken(authToken);
                request.setNotificationLink(notificationLink);
                request.setFileData(chunkData);
                
                // 上传分片
                lastResponse = uploadLargeFileChunk(request);
                System.out.println(lastResponse);
                
                if (lastResponse.isSuccess()) {
                    System.out.println("分片 " + chunkNumber + " 上传成功，当前索引 " + lastResponse.getFileIndex());
                    currentIndex = lastResponse.getFileIndexAsLong();
                    chunkNumber++;

                    if (lastResponse.isUploadComplete()) {
                        System.out.println("所有分片上传完成 文件ID: " + lastResponse.getId());
                        break;
                    }
                } else {
                    // 检查返回值是否为7（文件索引错误，需要从特定位置继续上传）
                    if (lastResponse.getCode() == 7) {
                        System.out.println("服务器返回码7（文件索引错误），需要从fileIndex位置继续上传");
                        long serverFileIndex = lastResponse.getFileIndexAsLong();
                        
                        System.out.println("当前客户端索引: " + currentIndex);
                        System.out.println("服务器期望索引: " + serverFileIndex);
                        
                        if (serverFileIndex > currentIndex) {
                            System.out.println("从服务器指定的索引 " + serverFileIndex + " 继续上传（跳过 " + (serverFileIndex - currentIndex) + " 字节）");
                            currentIndex = serverFileIndex;
                            // 重新计算分片号
                            chunkNumber = (currentIndex / chunkSize) + 1;
                            System.out.println("调整后的分片号: " + chunkNumber);
                            continue; // 继续下一轮循环，跳过当前分片
                        } else if (serverFileIndex < currentIndex) {
                            System.out.println("服务器期望的索引 " + serverFileIndex + " 小于当前索引 " + currentIndex + "，可能存在重复上传");
                            // 如果服务器索引小于当前索引，说明可能已经上传过了，直接使用服务器返回的索引
                            currentIndex = serverFileIndex;
                            chunkNumber = (currentIndex / chunkSize) + 1;
                            System.out.println("调整后的分片号: " + chunkNumber);
                            continue;
                        } else {
                            System.out.println("服务器索引与当前索引相同，继续正常上传");
                            currentIndex = lastResponse.getFileIndexAsLong();
                            chunkNumber++;
                        }
                    } else {
                        System.out.println("分片 " + chunkNumber + " 上传失败: " + lastResponse.getMessage() + " (错误码: " + lastResponse.getCode() + ")");
                        System.out.println("失败分片信息 -> 起始索引: " + currentIndex + ", 大小: " + currentChunkSize + " bytes (" + String.format("%.2f", currentChunkSize / (1024.0 * 1024.0)) + " MB)");
                        break;
                    }
                }

//                currentIndex = lastResponse.getFileIndexAsLong();

                logger.debug("Uploaded chunk: {}/{} bytes", currentIndex, fileSize);
                
                // 如果上传完成，退出循环
                if (lastResponse.isUploadComplete()) {
                    logger.info("File upload completed successfully. File ID: {}", lastResponse.getId());
                    break;
                }
            }
            
            return lastResponse;
            
        } catch (IOException e) {
            logger.error("Error uploading large file", e);
            throw new GuangYuYunException("File upload error: " + e.getMessage(), e);
        }
    }
    
    /**
     * 上传大文件（使用默认分片大小）
     * 
     * @param file 要上传的文件
     * @param authToken 认证令牌
     * @return 上传结果
     * @throws GuangYuYunException 当请求失败时抛出异常
     */
    public LargeFileUploadResponse uploadLargeFile(File file, String authToken) throws GuangYuYunException {
        return uploadLargeFile(file, authToken, null, null);
    }
    

    
    /**
     * 上传大文件（使用统一token和默认分片大小）
     * 
     * @param file 要上传的文件
     * @return 上传结果
     * @throws GuangYuYunException 当请求失败时抛出异常
     */
    public LargeFileUploadResponse uploadLargeFile(File file) throws GuangYuYunException {
        if (!hasAuthToken()) {
            throw new GuangYuYunException("AuthToken not set. Please call setAuthToken() first or use uploadLargeFile(file, authToken)");
        }
        return uploadLargeFile(file, this.authToken, null, null);
    }
    
    /**
     * 下载文件（支持断点续传）
     * 
     * @param fileId 文件ID
     * @param authToken 认证令牌
     * @param outputPath 输出文件路径
     * @return 下载结果
     * @throws GuangYuYunException 当请求失败时抛出异常
     */
    public FileDownloadResponse downloadFile(String fileId, String authToken, String outputPath) throws GuangYuYunException {
        FileDownloadRequest request = new FileDownloadRequest(fileId, authToken, outputPath);
        return downloadFile(request);
    }
    
    /**
     * 下载文件（使用统一token）
     * 
     * @param fileId 文件ID
     * @param outputPath 输出文件路径
     * @return 下载结果
     * @throws GuangYuYunException 当请求失败时抛出异常
     */
    public FileDownloadResponse downloadFile(String fileId, String outputPath) throws GuangYuYunException {
        if (!hasAuthToken()) {
            throw new GuangYuYunException("AuthToken not set. Please call setAuthToken() first or use downloadFile(fileId, authToken, outputPath)");
        }
        return downloadFile(fileId, this.authToken, outputPath);
    }
    
    /**
     * 下载文件（支持断点续传）
     * 
     * @param request 文件下载请求参数
     * @return 下载结果
     * @throws GuangYuYunException 当请求失败时抛出异常
     */
    public FileDownloadResponse downloadFile(FileDownloadRequest request) throws GuangYuYunException {
        validateDownloadRequest(request);
        
        try {
            String url = baseUrl + FILE_DOWNLOAD_ENDPOINT + "?id=" + request.getFileId();
            
            logger.info("Starting file download: {} to {}", request.getFileId(), request.getOutputPath());
            
            // 创建输出目录
            DownloadUtils.createDirectories(request.getOutputPath());
            
            // 检查是否需要断点续传
            long startByte = 0;
            if (request.isResumeEnabled() && DownloadUtils.fileExists(request.getOutputPath())) {
                startByte = DownloadUtils.getFileSize(request.getOutputPath());
                logger.info("Resuming download from byte: {}", startByte);
            }
            
            // 获取文件总大小
            long totalSize = getFileSize(request.getFileId(), request.getAuthToken());
            if (totalSize <= 0) {
                throw new GuangYuYunException("Unable to determine file size");
            }
            
            // 如果已经下载完成
            if (startByte >= totalSize) {
                FileDownloadResponse response = new FileDownloadResponse(true, "File already downloaded");
                response.setFilePath(request.getOutputPath());
                response.setTotalSize(totalSize);
                response.setDownloadedSize(totalSize);
                response.setFileId(request.getFileId());
                response.setCompleted(true);
                return response;
            }
            
            // 设置默认分片大小
            int chunkSize = request.getChunkSize() != null ? request.getChunkSize() : DownloadUtils.getDefaultChunkSize();
            if (!DownloadUtils.isValidChunkSize(chunkSize)) {
                chunkSize = DownloadUtils.getDefaultChunkSize();
            }
            
            long downloadedSize = startByte;
            long currentByte = startByte;
            
            int chunkNumber = 1;
            while (currentByte < totalSize) {
                long endByte = Math.min(currentByte + chunkSize - 1, totalSize - 1);
                
                System.out.println("=== 下载分片 " + chunkNumber + " ===");
                System.out.println("分片范围: " + currentByte + " - " + endByte + " bytes");
                System.out.println("分片大小: " + (endByte - currentByte + 1) + " bytes");
                System.out.println("开始下载分片...");
                
                long startTime = System.currentTimeMillis();
                byte[] chunkData = downloadFileChunk(request.getFileId(), request.getAuthToken(), currentByte, endByte);
                long endTime = System.currentTimeMillis();
                
                if (chunkData.length == 0) {
                    throw new GuangYuYunException("Received empty chunk data");
                }
                
                System.out.println("分片下载完成!");
                System.out.println("实际下载大小: " + chunkData.length + " bytes");
                System.out.println("下载耗时: " + (endTime - startTime) + " ms");
                System.out.println("下载速度: " + DownloadUtils.formatSpeed(chunkData.length * 1000 / (endTime - startTime)));
                
                // 追加数据到文件
                if (currentByte == 0) {
                    // 第一次下载，创建新文件
                    System.out.println("创建新文件: " + request.getOutputPath());
                    DownloadUtils.writeToFile(request.getOutputPath(), chunkData);
                } else {
                    // 断点续传，追加数据
                    System.out.println("追加数据到文件...");
                    DownloadUtils.appendToFile(request.getOutputPath(), chunkData);
                }
                
                downloadedSize += chunkData.length;
                currentByte = downloadedSize;
                
                double progress = (double) downloadedSize / totalSize * 100;
                System.out.println("总进度: " + downloadedSize + "/" + totalSize + " bytes (" + String.format("%.2f", progress) + "%)");
                System.out.println("剩余大小: " + (totalSize - downloadedSize) + " bytes");
                System.out.println();
                
                chunkNumber++;
            }
            
            FileDownloadResponse response = new FileDownloadResponse(true, "Download completed successfully");
            response.setFilePath(request.getOutputPath());
            response.setTotalSize(totalSize);
            response.setDownloadedSize(downloadedSize);
            response.setFileId(request.getFileId());
            response.setCompleted(true);
            
            logger.info("File download completed: {} ({})", request.getOutputPath(), 
                DownloadUtils.formatFileSize(downloadedSize));
            
            return response;
            
        } catch (IOException e) {
            logger.error("Error downloading file", e);
            throw new GuangYuYunException("File download error: " + e.getMessage(), e);
        }
    }
    
    /**
     * 下载文件分片
     * 
     * @param fileId 文件ID
     * @param authToken 认证令牌
     * @param startByte 起始字节
     * @param endByte 结束字节
     * @return 分片数据
     * @throws GuangYuYunException 当请求失败时抛出异常
     */
    public byte[] downloadFileChunk(String fileId, String authToken, long startByte, long endByte) throws GuangYuYunException {
        try {
            String url = baseUrl + FILE_DOWNLOAD_ENDPOINT + "?id=" + fileId;
            
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("AuthToken", authToken);
            httpGet.setHeader("Range", "bytes=" + startByte + "-" + endByte);
            
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            
            if (entity != null) {
                byte[] data = EntityUtils.toByteArray(entity);
                
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 206) { // Partial Content
                    return data;
                } else if (statusCode == 200) { // OK (full content)
                    return data;
                } else {
                    throw new GuangYuYunException("HTTP error: " + statusCode);
                }
            } else {
                throw new GuangYuYunException("Empty response from server");
            }
            
        } catch (IOException e) {
            logger.error("Error downloading file chunk", e);
            throw new GuangYuYunException("Network error: " + e.getMessage(), e);
        }
    }
    
    /**
     * 获取文件大小
     * 
     * @param fileId 文件ID
     * @param authToken 认证令牌
     * @return 文件大小
     * @throws GuangYuYunException 当请求失败时抛出异常
     */
    public long getFileSize(String fileId, String authToken) throws GuangYuYunException {
        try {
            String url = baseUrl + FILE_DOWNLOAD_ENDPOINT + "?id=" + fileId;
            
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("AuthToken", authToken);
            httpGet.setHeader("Range", "bytes=0-0"); // 只请求第一个字节
            
            HttpResponse response = httpClient.execute(httpGet);
            
            String contentRange = response.getFirstHeader("Content-Range") != null ? 
                response.getFirstHeader("Content-Range").getValue() : null;
            
            if (contentRange != null) {
                // Content-Range: bytes 0-0/1234567
                String[] parts = contentRange.split("/");
                if (parts.length == 2) {
                    return Long.parseLong(parts[1]);
                }
            }
            
            // 如果没有Content-Range头，尝试从Content-Length获取
            String contentLength = response.getFirstHeader("Content-Length") != null ? 
                response.getFirstHeader("Content-Length").getValue() : null;
            
            if (contentLength != null) {
                return Long.parseLong(contentLength);
            }
            
            // 如果都没有，返回-1表示无法确定大小
            return -1;
            
        } catch (IOException e) {
            logger.error("Error getting file size", e);
            throw new GuangYuYunException("Network error: " + e.getMessage(), e);
        }
    }
    

    
    /**
     * 获取文件大小（使用统一token）
     * 
     * @param fileId 文件ID
     * @return 文件大小
     * @throws GuangYuYunException 当请求失败时抛出异常
     */
    public long getFileSize(String fileId) throws GuangYuYunException {
        if (!hasAuthToken()) {
            throw new GuangYuYunException("AuthToken not set. Please call setAuthToken() first or use getFileSize(fileId, authToken)");
        }
        return getFileSize(fileId, this.authToken);
    }
    
    /**
     * 验证请求参数
     */
    private void validateRequest(TokenRequest request) throws GuangYuYunException {
        if (request == null) {
            throw new GuangYuYunException("Request cannot be null");
        }
        
        if (request.getAccount() == null || request.getAccount().trim().isEmpty()) {
            throw new GuangYuYunException("Account cannot be null or empty");
        }
        
        if (request.getApiKey() == null || request.getApiKey().trim().isEmpty()) {
            throw new GuangYuYunException("ApiKey cannot be null or empty");
        }
        
        if (request.getExpireTime() == null || request.getExpireTime() <= 0) {
            throw new GuangYuYunException("ExpireTime must be a positive number");
        }
    }
    
    /**
     * 验证大文件上传请求参数
     */
    private void validateLargeFileUploadRequest(LargeFileUploadRequest request) throws GuangYuYunException {
        if (request == null) {
            throw new GuangYuYunException("Request cannot be null");
        }
        
        if (request.getFileName() == null || request.getFileName().trim().isEmpty()) {
            throw new GuangYuYunException("FileName cannot be null or empty");
        }
        
        if (request.getFileMd5() == null || request.getFileMd5().trim().isEmpty()) {
            throw new GuangYuYunException("FileMd5 cannot be null or empty");
        }
        
        if (request.getFileSize() == null || request.getFileSize() <= 0) {
            throw new GuangYuYunException("FileSize must be a positive number");
        }
        
        if (request.getFileStartIndex() == null || request.getFileStartIndex() < 0) {
            throw new GuangYuYunException("FileStartIndex must be a non-negative number");
        }
        
        if (request.getAuthToken() == null || request.getAuthToken().trim().isEmpty()) {
            throw new GuangYuYunException("AuthToken cannot be null or empty");
        }
        
        if (request.getFileData() == null || request.getFileData().length == 0) {
            throw new GuangYuYunException("FileData cannot be null or empty");
        }
    }
    
    /**
     * 判断是否应该使用FileBody方式
     * SDK自动判断逻辑：
     * - 文件可读且不是符号链接时，优先使用FileBody（性能更好）
     * - 其他情况使用InputStreamBody
     * 
     * @param file 文件对象
     * @return true=使用FileBody, false=使用InputStreamBody
     */
    private boolean shouldUseFileBody(File file) {
        try {
            // 优先使用FileBody的条件：
            // 1. 文件可读
            // 2. 文件不是符号链接（避免潜在问题）
            // 3. 文件大小合理（小于50MB时优先使用FileBody）
            if (file.canRead()) {
                // 检查是否是符号链接
                String canonicalPath = file.getCanonicalPath();
                String absolutePath = file.getAbsolutePath();
                boolean isSymbolicLink = !canonicalPath.equals(absolutePath);
                
                // 小文件优先使用FileBody，大文件使用InputStreamBody（避免内存问题）
                long fileSize = file.length();
                long threshold = 50 * 1024 * 1024; // 50MB阈值
                
                if (!isSymbolicLink && fileSize < threshold) {
                    return true; // 使用FileBody
                }
            }
        } catch (IOException e) {
            logger.debug("Error checking file properties, will use InputStreamBody: {}", e.getMessage());
        }
        
        // 默认使用InputStreamBody（更安全，兼容性更好）
        return false;
    }
    
    /**
     * 验证小文件上传请求参数
     */
    private void validateSmallFileUploadRequest(SmallFileUploadRequest request) throws GuangYuYunException {
        if (request == null) {
            throw new GuangYuYunException("Request cannot be null");
        }
        
        if (request.getFile() == null) {
            throw new GuangYuYunException("File cannot be null");
        }
        
        if (!request.getFile().exists()) {
            throw new GuangYuYunException("File does not exist: " + request.getFile().getPath());
        }
        
        if (request.getFile().isDirectory()) {
            throw new GuangYuYunException("File cannot be a directory");
        }
        
        if (request.getAuthToken() == null || request.getAuthToken().trim().isEmpty()) {
            throw new GuangYuYunException("AuthToken cannot be null or empty");
        }
    }
    
    /**
     * 验证下载请求参数
     */
    private void validateDownloadRequest(FileDownloadRequest request) throws GuangYuYunException {
        if (request == null) {
            throw new GuangYuYunException("Request cannot be null");
        }
        
        if (request.getFileId() == null || request.getFileId().trim().isEmpty()) {
            throw new GuangYuYunException("FileId cannot be null or empty");
        }
        
        if (request.getAuthToken() == null || request.getAuthToken().trim().isEmpty()) {
            throw new GuangYuYunException("AuthToken cannot be null or empty");
        }
        
        if (request.getOutputPath() == null || request.getOutputPath().trim().isEmpty()) {
            throw new GuangYuYunException("OutputPath cannot be null or empty");
        }
        
        if (request.getChunkSize() != null && !DownloadUtils.isValidChunkSize(request.getChunkSize())) {
            throw new GuangYuYunException("Chunk size must be between 64KB and 10MB");
        }
    }
    
    /**
     * 关闭客户端资源
     */
    public void close() {
        try {
            if (httpClient instanceof org.apache.http.impl.client.CloseableHttpClient) {
                ((org.apache.http.impl.client.CloseableHttpClient) httpClient).close();
            }
        } catch (IOException e) {
            logger.warn("Error closing HTTP client", e);
        }
    }
}
