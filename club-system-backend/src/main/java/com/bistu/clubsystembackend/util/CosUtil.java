package com.bistu.clubsystembackend.util;

import com.bistu.clubsystembackend.config.CosProperties;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.region.Region;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

@Component
public class CosUtil {

    private final CosProperties cosProperties;
    private COSClient cosClient;

    public CosUtil(CosProperties cosProperties) {
        this.cosProperties = cosProperties;
    }

    private synchronized void initClientIfNecessary() {
        if (this.cosClient != null) {
            return;
        }
        validateProperties();
        COSCredentials credentials = new BasicCOSCredentials(cosProperties.getSecretId(), cosProperties.getSecretKey());
        ClientConfig clientConfig = new ClientConfig(new Region(cosProperties.getRegion()));
        this.cosClient = new COSClient(credentials, clientConfig);
    }

    @PreDestroy
    public void destroy() {
        if (this.cosClient != null) {
            this.cosClient.shutdown();
        }
    }

    public String upload(InputStream inputStream, String originalFilename, String contentType, long contentLength) {
        ensureClientInitialized();
        Objects.requireNonNull(inputStream, "inputStream must not be null");
        String key = buildObjectKey(originalFilename);
        ObjectMetadata metadata = new ObjectMetadata();
        if (contentLength >= 0) {
            metadata.setContentLength(contentLength);
        }
        if (StringUtils.hasText(contentType)) {
            metadata.setContentType(contentType.trim());
        }
        try (InputStream in = inputStream) {
            PutObjectRequest request = new PutObjectRequest(cosProperties.getBucket(), key, in, metadata);
            cosClient.putObject(request);
            return key;
        } catch (Exception e) {
            throw new BusinessException(BizCode.SYSTEM_ERROR.getCode(), "COS upload failed");
        }
    }

    public String getUrl(String key) {
        if (!StringUtils.hasText(key)) {
            throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "key must not be blank");
        }
        String domain = trimTrailingSlash(cosProperties.getDomain());
        return domain + "/" + removeLeadingSlash(key);
    }

    private String buildObjectKey(String originalFilename) {
        String suffix = extractSafeSuffix(originalFilename);
        String fileName = UUID.randomUUID().toString().replace("-", "") + suffix;
        String folder = normalizeFolder(cosProperties.getFolder());
        return folder + fileName;
    }

    private String extractSafeSuffix(String originalFilename) {
        if (!StringUtils.hasText(originalFilename)) {
            return "";
        }
        String filename = originalFilename.trim();
        int index = filename.lastIndexOf('.');
        if (index < 0 || index == filename.length() - 1) {
            return "";
        }
        String suffix = filename.substring(index).toLowerCase(Locale.ROOT);
        return suffix.matches("\\.[a-z0-9]{1,10}") ? suffix : "";
    }

    private String normalizeFolder(String folder) {
        if (!StringUtils.hasText(folder)) {
            return "";
        }
        String normalized = removeLeadingSlash(folder.trim());
        return normalized.endsWith("/") ? normalized : normalized + "/";
    }

    private String removeLeadingSlash(String value) {
        String result = value == null ? "" : value;
        while (result.startsWith("/")) {
            result = result.substring(1);
        }
        return result;
    }

    private String trimTrailingSlash(String value) {
        String result = value == null ? "" : value.trim();
        while (result.endsWith("/")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    private void ensureClientInitialized() {
        initClientIfNecessary();
    }

    private void validateProperties() {
        if (!StringUtils.hasText(cosProperties.getSecretId())
                || !StringUtils.hasText(cosProperties.getSecretKey())
                || !StringUtils.hasText(cosProperties.getRegion())
                || !StringUtils.hasText(cosProperties.getBucket())
                || !StringUtils.hasText(cosProperties.getDomain())) {
            throw new BusinessException(BizCode.SYSTEM_ERROR.getCode(), "COS properties are incomplete");
        }
    }
}
