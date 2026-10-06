package com.blog.infrastructure.storage;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.blog.application.service.StorageService;
import com.blog.domain.common.BusinessException;
import com.blog.infrastructure.config.QiniuStorageProperties;
import com.blog.interfaces.dto.response.UploadResponse;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 七牛云对象存储实现。
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class QiniuStorageService implements StorageService {

    private static final DateTimeFormatter DATE_PATH_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    private static final Pattern HTML_URL_ATTR_PATTERN =
            Pattern.compile("(?i)(src|href|data-href)=(['\"])(.*?)\\2");

    private final QiniuStorageProperties properties;

    @Override
    public UploadResponse uploadImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(400, "图片不能为空");
        }
        String contentType = file.getContentType();
        if (StrUtil.isBlank(contentType) || !contentType.toLowerCase(Locale.ROOT).startsWith("image/")) {
            throw new BusinessException(400, "只支持上传图片文件");
        }
        return upload(file, "images", "image");
    }

    @Override
    public UploadResponse uploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(400, "文件不能为空");
        }
        return upload(file, "files", "file");
    }

    private UploadResponse upload(MultipartFile file, String directory, String type) {
        validateConfig();
        validateSize(file);

        String originalFilename = StringUtils.cleanPath(
                file.getOriginalFilename() != null ? file.getOriginalFilename() : "file");
        String key = buildObjectKey(originalFilename, directory);
        String token = createUploadToken(key);

        long startTime = System.currentTimeMillis();
        log.info("开始上传到七牛云: key={}, filename={}, size={}, contentType={}, region={}",
                key, originalFilename, file.getSize(), file.getContentType(), properties.getRegion());
        try {
            Configuration configuration = buildConfiguration();
            UploadManager uploadManager = new UploadManager(configuration);
            Response response = uploadManager.put(file.getBytes(), key, token);
            if (!response.isOK()) {
                log.warn("七牛云上传返回非成功状态: key={}, status={}, body={}",
                        key, response.statusCode, response.bodyString());
                throw new BusinessException("七牛云上传失败");
            }
            log.info("七牛云上传成功: key={}, durationMs={}", key, System.currentTimeMillis() - startTime);
            return new UploadResponse(
                    buildAccessibleUrl(key),
                    key,
                    originalFilename,
                    file.getContentType(),
                    file.getSize(),
                    type
            );
        } catch (QiniuException e) {
            log.warn("七牛云上传异常: key={}, durationMs={}, response={}",
                    key, System.currentTimeMillis() - startTime, e.response, e);
            throw new BusinessException("七牛云上传失败：" + e.response);
        } catch (IOException e) {
            log.warn("读取上传文件失败: filename={}, size={}", originalFilename, file.getSize(), e);
            throw new BusinessException("读取上传文件失败");
        }
    }

    @Override
    public String resolveUrl(String urlOrKey) {
        if (StrUtil.isBlank(urlOrKey)) {
            return urlOrKey;
        }
        String key = extractObjectKey(urlOrKey);
        return key != null ? buildAccessibleUrl(key) : urlOrKey;
    }

    @Override
    public String resolveContentUrls(String content) {
        if (StrUtil.isBlank(content)) {
            return content;
        }
        Matcher matcher = HTML_URL_ATTR_PATTERN.matcher(content);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String resolvedUrl = resolveUrl(matcher.group(3));
            String replacement = matcher.group(1) + "=" + matcher.group(2) + resolvedUrl + matcher.group(2);
            matcher.appendReplacement(buffer, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    private Configuration buildConfiguration() {
        Configuration configuration = new Configuration(resolveRegion());
        configuration.connectTimeout = positiveOrDefault(properties.getConnectTimeoutSeconds(), 10);
        configuration.readTimeout = positiveOrDefault(properties.getReadTimeoutSeconds(), 60);
        configuration.writeTimeout = positiveOrDefault(properties.getWriteTimeoutSeconds(), 60);
        configuration.retryMax = Math.max(properties.getRetryMax() != null ? properties.getRetryMax() : 1, 0);
        return configuration;
    }

    private Region resolveRegion() {
        String region = properties.getRegion();
        if (StrUtil.isBlank(region) || "auto".equalsIgnoreCase(region)) {
            return Region.autoRegion();
        }

        return switch (region.trim().toLowerCase(Locale.ROOT)) {
            case "huadong", "region0", "z0" -> Region.huadong();
            case "huabei", "region1", "z1" -> Region.huabei();
            case "huanan", "region2", "z2" -> Region.huanan();
            case "beimei", "regionna0", "na0" -> Region.beimei();
            case "xinjiapo", "regionas0", "as0" -> Region.xinjiapo();
            case "cn-east-2", "huadongzhejiang2", "zhejiang2" -> Region.huadongZheJiang2();
            default -> Region.createWithRegionId(region.trim());
        };
    }

    private int positiveOrDefault(Integer value, int fallback) {
        return value != null && value > 0 ? value : fallback;
    }

    private void validateConfig() {
        if (!Boolean.TRUE.equals(properties.getEnabled())) {
            throw new BusinessException("七牛云存储未启用，请先配置 blog.storage.qiniu.enabled=true");
        }
        if (StrUtil.hasBlank(properties.getAccessKey(), properties.getSecretKey(),
                properties.getBucket(), properties.getDomain())) {
            throw new BusinessException("七牛云存储配置不完整");
        }
    }

    private void validateSize(MultipartFile file) {
        if (properties.getMaxFileSize() != null && file.getSize() > properties.getMaxFileSize().toBytes()) {
            throw new BusinessException(400, "文件大小不能超过 " + properties.getMaxFileSize());
        }
    }

    private String createUploadToken(String key) {
        Auth auth = Auth.create(properties.getAccessKey(), properties.getSecretKey());
        long expires = properties.getTokenExpireSeconds() != null ? properties.getTokenExpireSeconds() : 3600L;
        return auth.uploadToken(properties.getBucket(), key, expires, null);
    }

    private String buildObjectKey(String originalFilename, String directory) {
        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex >= 0 && dotIndex < originalFilename.length() - 1) {
            extension = originalFilename.substring(dotIndex).toLowerCase(Locale.ROOT);
        }

        String prefix = normalizePathSegment(properties.getPrefix());
        String datePath = LocalDate.now().format(DATE_PATH_FORMATTER);
        String filename = IdUtil.fastSimpleUUID() + extension;
        return String.join("/", prefix, directory, datePath, filename);
    }

    private String buildAccessibleUrl(String key) {
        String publicUrl = buildPublicUrl(key);
        if (!Boolean.TRUE.equals(properties.getPrivateDownloadEnabled())) {
            return publicUrl;
        }
        Auth auth = Auth.create(properties.getAccessKey(), properties.getSecretKey());
        long expires = properties.getDownloadUrlExpireSeconds() != null
                ? properties.getDownloadUrlExpireSeconds()
                : 315360000L;
        return auth.privateDownloadUrl(publicUrl, expires);
    }

    private String buildPublicUrl(String key) {
        String domain = StrUtil.removeSuffix(properties.getDomain().trim(), "/");
        if (!domain.startsWith("http://") && !domain.startsWith("https://")) {
            domain = normalizeProtocol(properties.getProtocol()) + "://" + domain;
        }
        return domain + "/" + StrUtil.removePrefix(key, "/");
    }

    private String extractObjectKey(String urlOrKey) {
        String value = urlOrKey.trim();
        if (value.startsWith("data:") || value.startsWith("blob:")) {
            return null;
        }
        if (!value.startsWith("http://") && !value.startsWith("https://")) {
            return looksLikeObjectKey(value) ? StrUtil.removePrefix(value, "/") : null;
        }

        try {
            URI uri = new URI(value);
            String configuredHost = configuredHost();
            if (configuredHost.equalsIgnoreCase(uri.getHost())) {
                return StrUtil.removePrefix(uri.getPath(), "/");
            }
        } catch (URISyntaxException ignored) {
            return null;
        }
        return null;
    }

    private boolean looksLikeObjectKey(String value) {
        String normalized = StrUtil.removePrefix(value, "/");
        String prefix = normalizePathSegment(properties.getPrefix()) + "/";
        return normalized.startsWith(prefix) || normalized.startsWith("images/") || normalized.startsWith("files/");
    }

    private String configuredHost() {
        String domain = properties.getDomain().trim();
        if (!domain.startsWith("http://") && !domain.startsWith("https://")) {
            domain = normalizeProtocol(properties.getProtocol()) + "://" + domain;
        }
        try {
            return new URI(domain).getHost();
        } catch (URISyntaxException e) {
            return StrUtil.removePrefix(StrUtil.removePrefix(domain, "https://"), "http://");
        }
    }

    private String normalizeProtocol(String value) {
        if ("https".equalsIgnoreCase(value)) {
            return "https";
        }
        return "http";
    }

    private String normalizePathSegment(String value) {
        if (StrUtil.isBlank(value)) {
            return "blog";
        }
        String normalized = value.trim();
        normalized = StrUtil.removePrefix(normalized, "/");
        normalized = StrUtil.removeSuffix(normalized, "/");
        return StrUtil.isBlank(normalized) ? "blog" : normalized;
    }
}
