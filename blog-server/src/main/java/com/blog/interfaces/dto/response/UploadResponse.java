package com.blog.interfaces.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件上传响应。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadResponse {
    private String url;
    private String key;
    private String originalFilename;
    private String contentType;
    private Long size;
    private String type;
}
