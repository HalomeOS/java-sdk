# GuangYuYun Java SDK

这是一个用于调用 GuangYuYun API 的 Java SDK，提供了简单易用的接口来获取访问令牌、上传文件（小文件和大文件）和下载文件。

## 功能特性

- 简单易用的 API 接口
- 完整的参数验证
- 详细的异常处理
- 支持 JSON 序列化/反序列化
- **小文件上传（单文件最大 100MB）**
- **大文件上传支持断点续传**
- **自动文件分片处理**
- **MD5 文件校验**
- **异步通知支持**
- **文件下载支持断点续传**
- **Range 协议支持**
- **详细的下载进度监控和日志**
- **文件索引错误自动处理**
- **10 位时间戳支持**
- **根据文件路径上传**
- 完整的单元测试
- Maven 依赖管理

## 安装

### Maven

在你的`pom.xml`文件中添加以下依赖（与当前 `pom.xml` 同步）：

```xml
<dependency>
    <groupId>com.guangyuyun</groupId>
    <artifactId>guangyuyun-sdk</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 手动安装

1. 下载 SDK jar 包
2. 将 jar 包添加到你的项目 classpath 中
3. 确保包含所有必要的依赖项

## 快速开始

### 基本用法

```java
import com.guangyuyun.sdk.GuangYuYunClient;
import com.guangyuyun.sdk.exception.GuangYuYunException;
import com.guangyuyun.sdk.model.TokenResponse;

public class QuickStart {
    public static void main(String[] args) {
        GuangYuYunClient client = new GuangYuYunClient();

        try {
            String account = "your_account";
            String apiKey = "your_api_key";
            Long expireTime = System.currentTimeMillis() / 1000 + 3600; // 10位时间戳，1小时后过期

            TokenResponse response = client.createToken(account, apiKey, expireTime);

            if (response.isSuccess()) {
                System.out.println("Token: " + response.getToken());
                System.out.println("Expire Time: " + response.getExpireTime());
            } else {
                System.out.println("Error: " + response.getMessage());
            }

        } catch (GuangYuYunException e) {
            System.err.println("SDK Error: " + e.getMessage());
        } finally {
            client.close();
        }
    }
}
```

### 使用请求对象

```java
import com.guangyuyun.sdk.GuangYuYunClient;
import com.guangyuyun.sdk.model.TokenRequest;
import com.guangyuyun.sdk.model.TokenResponse;

GuangYuYunClient client = new GuangYuYunClient();

TokenRequest request = new TokenRequest();
request.setAccount("your_account");
request.setApiKey("your_api_key");
request.setExpireTime(System.currentTimeMillis() / 1000 + 3600);

TokenResponse response = client.createToken(request);
```

### 自定义服务器地址

```java
GuangYuYunClient client = new GuangYuYunClient("https://your-custom-server.com");
```

## 详细使用指南

### 小文件上传

适用于单文件大小不超过 100MB 的文件上传。

```java
import com.guangyuyun.sdk.GuangYuYunClient;
import com.guangyuyun.sdk.exception.GuangYuYunException;
import com.guangyuyun.sdk.model.SmallFileUploadResponse;
import java.io.File;

GuangYuYunClient client = new GuangYuYunClient();

try {
    // 根据文件路径创建文件
    String filePath = "path/to/your/small/file.txt";
    File file = new File(filePath);

    if (!file.exists()) {
        System.err.println("文件不存在: " + filePath);
        return;
    }

    String authToken = "your_auth_token";

    // 基本上传
    SmallFileUploadResponse response = client.uploadSmallFile(file, authToken);

    if (response.isSuccess()) {
        System.out.println("✓ 文件上传成功!");
        System.out.println("文件ID: " + response.getFileId());
        System.out.println("返回消息: " + response.getMessage());
    } else {
        System.out.println("✗ 文件上传失败: " + response.getMessage());
        System.out.println("错误码: " + response.getCode());
    }

    // 使用统一token上传（先设置token，然后直接调用）
    client.setAuthToken(authToken);
    SmallFileUploadResponse response2 = client.uploadSmallFile(file);

} catch (GuangYuYunException e) {
    System.err.println("上传失败: " + e.getMessage());
    if (e.getCode() != null) {
        System.err.println("错误码: " + e.getCode());
    }
} finally {
    client.close();
}
```

**注意事项：**

- 单文件大小不能超过 100MB，超过请使用大文件上传接口
- 不支持多文件上传
- 文件大小会在上传前自动验证

### 大文件上传

```java
import com.guangyuyun.sdk.GuangYuYunClient;
import com.guangyuyun.sdk.exception.GuangYuYunException;
import com.guangyuyun.sdk.model.LargeFileUploadResponse;
import java.io.File;

GuangYuYunClient client = new GuangYuYunClient();

try {
    // 根据文件路径创建文件
    String filePath = "path/to/your/large/file.zip";
    File file = new File(filePath);

    if (!file.exists()) {
        System.err.println("文件不存在: " + filePath);
        return;
    }

    String authToken = "your_auth_token";

    // 基本上传（默认分片大小）
    LargeFileUploadResponse response = client.uploadLargeFile(file, authToken);

    if (response.isSuccess()) {
        System.out.println("文件上传成功!");
        System.out.println("文件ID: " + response.getId());
        System.out.println("当前索引: " + response.getFileIndex());
        System.out.println("是否完成: " + response.isUploadComplete());
    } else {
        System.out.println("文件上传失败: " + response.getMessage());
        System.out.println("错误码: " + response.getCode());
    }

    // 自定义分片大小上传（2MB）
    LargeFileUploadResponse response2 = client.uploadLargeFile(file, authToken, 2 * 1024 * 1024, null);

    // 带异步通知链接上传
    String notificationUrl = "https://your-server.com/upload-notification";
    LargeFileUploadResponse response3 = client.uploadLargeFile(file, authToken, null, notificationUrl);

} catch (GuangYuYunException e) {
    System.err.println("上传失败: " + e.getMessage());
} finally {
    client.close();
}
```

### 文件下载

```java
import com.guangyuyun.sdk.GuangYuYunClient;
import com.guangyuyun.sdk.exception.GuangYuYunException;
import com.guangyuyun.sdk.model.FileDownloadRequest;
import com.guangyuyun.sdk.model.FileDownloadResponse;
import com.guangyuyun.sdk.util.DownloadUtils;

GuangYuYunClient client = new GuangYuYunClient();

try {
    String fileId = "your_file_id";
    String authToken = "your_auth_token";
    String outputPath = "downloaded_file.zip";

    // 基本下载（支持断点续传）
    FileDownloadResponse response = client.downloadFile(fileId, authToken, outputPath);

    if (response.isSuccess()) {
        System.out.println("✓ 文件下载成功!");
        System.out.println("文件路径: " + response.getFilePath());
        System.out.println("文件大小: " + DownloadUtils.formatFileSize(response.getTotalSize()));
        System.out.println("下载大小: " + DownloadUtils.formatFileSize(response.getDownloadedSize()));
        System.out.println("是否完成: " + response.isCompleted());
    }

    // 自定义分片大小下载（使用请求对象）
    FileDownloadRequest req = new FileDownloadRequest();
    req.setFileId(fileId);
    req.setAuthToken(authToken);
    req.setOutputPath(outputPath);
    req.setChunkSize(1 * 1024 * 1024); // 1MB
    req.setResumeEnabled(true);
    FileDownloadResponse response2 = client.downloadFile(req);

    // 获取文件大小
    long fileSize = client.getFileSize(fileId, authToken);

} catch (GuangYuYunException e) {
    System.err.println("下载失败: " + e.getMessage());
} finally {
    client.close();
}
```

## API 文档

### GuangYuYunClient

主要的客户端类，提供所有 API 功能。

**Token 相关:**

- `createToken(String account, String apiKey, Long expireTime)` - 创建访问令牌
- `createToken(TokenRequest request)` - 使用请求对象创建访问令牌

**小文件上传相关:**

- `uploadSmallFile(File file, String authToken)` - 上传小文件（单文件最大 100MB）
- `uploadSmallFile(File file)` - 上传小文件（使用统一 token）
- `uploadSmallFile(SmallFileUploadRequest request)` - 使用请求对象上传小文件
- **自动文件大小验证** - 上传前自动检查文件大小，超过 100MB 会提示使用大文件上传接口

**大文件上传相关:**

- `uploadLargeFile(File file, String authToken)` - 上传大文件（使用默认分片大小）
- `uploadLargeFile(File file, String authToken, Integer chunkSize, String notificationLink)` - 上传大文件（自定义参数）
- `uploadLargeFileChunk(LargeFileUploadRequest request)` - 上传单个文件分片
- **支持文件索引错误自动处理** - 当服务器返回错误码 7 时，SDK 会自动从服务器指定的位置继续上传

**文件下载相关:**

- `downloadFile(String fileId, String authToken, String outputPath)` - 下载文件（支持断点续传）
- `downloadFile(FileDownloadRequest request)` - 下载文件（使用请求对象）
- `downloadFileChunk(String fileId, String authToken, long startByte, long endByte)` - 下载文件分片
- `getFileSize(String fileId, String authToken)` - 获取文件大小
- **详细的下载进度监控** - 每片下载都会输出详细的日志信息，包括分片范围、下载速度、进度等

**Token 管理方法:**

- `setAuthToken(String authToken)` - 设置统一的认证 token
- `getAuthToken()` - 获取当前认证 token
- `hasAuthToken()` - 检查是否已设置认证 token
- `clearAuthToken()` - 清除认证 token

**通用方法:**

- `close()` - 关闭客户端并释放资源

## 数据模型

### TokenRequest

令牌请求参数类。

#### 字段

- `account` (String) - 账号，必填
- `apiKey` (String) - API 密钥，必填
- `expireTime` (Long) - 到期时间戳（10 位），必填

### TokenResponse

令牌响应结果类。

#### 字段

- `code` (Integer) - 响应状态码
- `message` (String) - 响应消息
- `data` (Object) - 响应数据（支持字符串和对象两种格式）

#### 方法

- `isSuccess()` - 判断请求是否成功
- `getToken()` - 获取 Token 字符串（自动处理字符串和对象格式）
- `getExpireTime()` - 获取过期时间（兼容 TokenData 格式）

### SmallFileUploadRequest

小文件上传请求参数类。

#### 字段

- `file` (File) - 要上传的文件，必填
- `authToken` (String) - 认证令牌，必填

### SmallFileUploadResponse

小文件上传响应结果类。

#### 字段

- `code` (Integer) - 响应状态码（200 表示成功）
- `message` (String) - 响应消息
- `data` (String) - 文件 ID

#### 方法

- `isSuccess()` - 判断请求是否成功（code == 200）
- `getFileId()` - 获取文件 ID（data 字段的别名）

### LargeFileUploadRequest

大文件上传请求参数类。

#### 字段

- `fileName` (String) - 文件名，必填
- `fileMd5` (String) - 文件 MD5 值，必填
- `fileSize` (Long) - 文件大小，必填
- `fileStartIndex` (Long) - 文件起始索引，必填
- `authToken` (String) - 认证令牌，必填
- `fileData` (byte[]) - 文件数据，必填
- `notificationLink` (String) - 异步通知链接，可选

### LargeFileUploadResponse

大文件上传响应结果类。

#### 字段

- `code` (Integer) - 响应状态码
- `message` (String) - 响应消息
- `id` (String) - 文件 ID
- `fileIndex` (String) - 当前文件索引
- `uploadComplete` (Boolean) - 是否上传完成

#### 方法

- `isSuccess()` - 判断请求是否成功
- `isUploadComplete()` - 判断是否上传完成
- `getFileIndexAsLong()` - 获取文件索引（Long 类型）

### FileDownloadRequest

文件下载请求参数类。

#### 字段

- `fileId` (String) - 文件 ID，必填
- `authToken` (String) - 认证令牌，必填
- `outputPath` (String) - 输出文件路径，必填
- `chunkSize` (Integer) - 分片大小，可选
- `resumeEnabled` (Boolean) - 是否启用断点续传，可选

### FileDownloadResponse

文件下载响应结果类。

#### 字段

- `success` (Boolean) - 是否成功
- `message` (String) - 响应消息
- `filePath` (String) - 文件路径
- `totalSize` (Long) - 文件总大小
- `downloadedSize` (Long) - 已下载大小
- `fileId` (String) - 文件 ID
- `completed` (Boolean) - 是否完成

#### 方法

- `isSuccess()` - 判断请求是否成功
- `isCompleted()` - 判断是否下载完成
- `getProgressPercentage()` - 获取下载进度百分比

## 错误处理

SDK 会抛出`GuangYuYunException`异常来处理各种错误情况：

- 参数验证失败
- 网络连接错误
- API 调用失败
- JSON 解析错误
- 文件不存在或无法读取
- 分片大小超出范围（1MB-10MB）
- MD5 计算失败
- 下载分片大小超出范围（64KB-10MB）
- Range 请求失败
- 文件大小获取失败
- **文件索引错误（错误码 7）** - SDK 会自动处理，从服务器指定的位置继续上传

**小文件上传错误码：**

- `200` - 成功
- `4` - 不支持多文件上传
- `5` - 单文件大小不能超过 100M，大文件上传请调用大文件上传接口
- `6` - 文件错误
- `7` - 添加到分布式存储系统错误
- `8` - 写入数据库错误

```java
try {
    // Token 相关错误处理
    TokenResponse response = client.createToken(account, apiKey, expireTime);
    // 处理成功响应
} catch (GuangYuYunException e) {
    // 处理SDK异常
    System.err.println("Error: " + e.getMessage());
    if (e.getCode() != null) {
        System.err.println("Error Code: " + e.getCode());
    }
}


try {
    // 小文件上传错误处理
    SmallFileUploadResponse smallUploadResponse = client.uploadSmallFile(file, authToken);
    if (smallUploadResponse.isSuccess()) {
        System.out.println("小文件上传成功! 文件ID: " + smallUploadResponse.getFileId());
    } else {
        System.out.println("小文件上传失败: " + smallUploadResponse.getMessage());
        System.out.println("错误码: " + smallUploadResponse.getCode());

        // 处理不同错误码
        switch (smallUploadResponse.getCode()) {
            case 4:
                System.err.println("不支持多文件上传");
                break;
            case 5:
                System.err.println("文件大小超过100MB，请使用大文件上传接口");
                break;
            case 6:
                System.err.println("文件错误，请检查文件是否损坏");
                break;
            case 7:
                System.err.println("添加到分布式存储系统错误");
                break;
            case 8:
                System.err.println("写入数据库错误");
                break;
        }
    }
} catch (GuangYuYunException e) {
    System.err.println("小文件上传失败: " + e.getMessage());
    if (e.getCode() != null) {
        System.err.println("错误码: " + e.getCode());
    }
}

try {
    // 大文件上传错误处理
    LargeFileUploadResponse uploadResponse = client.uploadLargeFile(file, authToken);
    if (uploadResponse.isSuccess()) {
        System.out.println("大文件上传成功! 文件ID: " + uploadResponse.getId());
    } else {
        System.out.println("大文件上传失败: " + uploadResponse.getMessage());
        System.out.println("错误码: " + uploadResponse.getCode());

        // 处理错误码7（文件索引错误）
        if (uploadResponse.getCode() == 7) {
            System.out.println("检测到文件索引错误，SDK会自动处理");
            System.out.println("服务器期望的索引: " + uploadResponse.getFileIndex());
        }
    }
} catch (GuangYuYunException e) {
    System.err.println("大文件上传失败: " + e.getMessage());
    // 可以根据错误类型进行不同处理
    if (e.getMessage().contains("分片大小")) {
        System.err.println("请调整分片大小到1MB-10MB范围内");
    }
}

try {
    // 文件下载错误处理
    FileDownloadResponse downloadResponse = client.downloadFile(fileId, authToken, outputPath);
    if (downloadResponse.isSuccess()) {
        System.out.println("文件下载成功! 路径: " + downloadResponse.getFilePath());
    }
} catch (GuangYuYunException e) {
    System.err.println("文件下载失败: " + e.getMessage());
}
```

## 开发指南

### 构建项目

```bash
mvn clean compile
mvn test
mvn package
```

## 版本历史

### v1.0.0 (最新)

- ✅ 基本 Token 创建功能（10 位时间戳）
- ✅ **小文件上传（单文件最大 100MB，multipart/form-data）**
- ✅ 大文件上传（分片、断点续传、错误码 7 续传指引）
- ✅ 文件下载（断点续传、详细分片日志、进度统计）
- ✅ 获取文件大小（支持 Range/Content-Length 兼容）
- ✅ MD5 校验、分片大小校验
- ✅ 统一 Token 管理（setAuthToken/getAuthToken/hasAuthToken/clearAuthToken）

## 许可证

MIT License

## 支持

如有问题或建议，请联系开发团队。
