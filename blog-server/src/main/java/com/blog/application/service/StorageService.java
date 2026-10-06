package com.blog.application.service;

import com.blog.interfaces.dto.response.UploadResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * 外部对象存储服务。
 */
public interface StorageService {

    UploadResponse uploadImage(MultipartFile file);

    UploadResponse uploadFile(MultipartFile file);

    String resolveUrl(String urlOrKey);

    String resolveContentUrls(String content);
}
