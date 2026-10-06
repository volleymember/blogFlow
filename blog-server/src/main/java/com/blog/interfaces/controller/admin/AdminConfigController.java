package com.blog.interfaces.controller.admin;

import com.blog.application.service.SiteConfigService;
import com.blog.domain.common.Result;
import com.blog.interfaces.dto.response.DashboardStatsResponse;
import com.blog.interfaces.dto.response.SiteConfigVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 后台配置管理控制器
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminConfigController {

    private final SiteConfigService siteConfigService;

    /**
     * 仪表盘统计
     */
    @GetMapping("/dashboard/stats")
    public Result<DashboardStatsResponse> getStats() {
        return Result.success(siteConfigService.getDashboardStats());
    }

    /**
     * 获取网站配置
     */
    @GetMapping("/config")
    public Result<SiteConfigVO> getConfig() {
        return Result.success(siteConfigService.getSiteConfig());
    }

    /**
     * 更新网站配置
     */
    @PutMapping("/config")
    public Result<Void> updateConfig(@RequestBody Map<String, String> configMap) {
        siteConfigService.updateSiteConfig(configMap);
        return Result.success();
    }
}
