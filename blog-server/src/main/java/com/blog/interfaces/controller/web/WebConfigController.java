package com.blog.interfaces.controller.web;

import com.blog.application.service.SiteConfigService;
import com.blog.domain.common.Result;
import com.blog.interfaces.dto.response.SiteConfigVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 前台配置控制器
 */
@RestController
@RequestMapping("/api/web/config")
@RequiredArgsConstructor
public class WebConfigController {

    private final SiteConfigService siteConfigService;

    /**
     * 获取网站配置
     */
    @GetMapping
    public Result<SiteConfigVO> getConfig() {
        return Result.success(siteConfigService.getSiteConfig());
    }
}
