package com.blog.interfaces.controller.admin;

import com.blog.application.service.StorageService;
import com.blog.domain.common.Result;
import com.blog.interfaces.dto.response.UploadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 后台上传控制器。
 */
@RestController
@RequestMapping("/api/admin/upload")
@RequiredArgsConstructor
public class AdminUploadController {

    private final StorageService storageService;

    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<UploadResponse> uploadImage(@RequestParam("file") MultipartFile file) {
        return Result.success(storageService.uploadImage(file));
    }

    @PostMapping(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<UploadResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        return Result.success(storageService.uploadFile(file));
    }
}
