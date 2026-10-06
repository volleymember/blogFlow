package com.blog.interfaces.dto.response;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 网站配置响应
 */
@Data
public class SiteConfigVO {
    private String title;
    private String subtitle;
    private String description;
    private String keywords;
    private String logo;
    private String favicon;
    private String icp;
    private String github;
    private String email;
    private String wechat;
    private Boolean commentAudit;
    private String themeMode;

    public static SiteConfigVO fromMap(Map<String, String> configMap) {
        SiteConfigVO vo = new SiteConfigVO();
        vo.setTitle(configMap.getOrDefault("site_title", ""));
        vo.setSubtitle(configMap.getOrDefault("site_subtitle", ""));
        vo.setDescription(configMap.getOrDefault("site_description", ""));
        vo.setKeywords(configMap.getOrDefault("site_keywords", ""));
        vo.setLogo(configMap.getOrDefault("site_logo", ""));
        vo.setFavicon(configMap.getOrDefault("site_favicon", ""));
        vo.setIcp(configMap.getOrDefault("site_icp", ""));
        vo.setGithub(configMap.getOrDefault("social_github", ""));
        vo.setEmail(configMap.getOrDefault("social_email", ""));
        vo.setWechat(configMap.getOrDefault("social_wechat", ""));
        vo.setCommentAudit(Boolean.parseBoolean(configMap.getOrDefault("comment_audit", "true")));
        vo.setThemeMode(configMap.getOrDefault("theme_mode", "light"));
        return vo;
    }
}
