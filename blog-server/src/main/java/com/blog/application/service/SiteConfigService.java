package com.blog.application.service;

import com.blog.interfaces.dto.response.DashboardStatsResponse;
import com.blog.interfaces.dto.response.SiteConfigVO;

import java.util.Map;

/**
 * 网站配置服务接口
 */
public interface SiteConfigService {

    SiteConfigVO getSiteConfig();

    void updateSiteConfig(Map<String, String> configMap);

    DashboardStatsResponse getDashboardStats();

    void recordVisit(String ip, String url, Long articleId, String userAgent, String referer);
}
