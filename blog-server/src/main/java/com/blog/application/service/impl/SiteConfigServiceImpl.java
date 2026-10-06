package com.blog.application.service.impl;

import com.blog.application.service.ArticleService;
import com.blog.application.service.SiteConfigService;
import com.blog.domain.common.Constants;
import com.blog.domain.entity.Article;
import com.blog.domain.entity.SiteConfig;
import com.blog.domain.entity.VisitLog;
import com.blog.domain.repository.ArticleRepository;
import com.blog.domain.repository.CategoryRepository;
import com.blog.domain.repository.CommentRepository;
import com.blog.infrastructure.persistence.mapper.ArticleMapper;
import com.blog.infrastructure.persistence.mapper.SiteConfigMapper;
import com.blog.infrastructure.persistence.mapper.VisitLogMapper;
import com.blog.interfaces.dto.response.ArticleVO;
import com.blog.interfaces.dto.response.DashboardStatsResponse;
import com.blog.interfaces.dto.response.SiteConfigVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 网站配置服务实现
 */
@Service
@RequiredArgsConstructor
public class SiteConfigServiceImpl implements SiteConfigService {

    private final ArticleRepository articleRepository;
    private final CategoryRepository categoryRepository;
    private final CommentRepository commentRepository;
    private final ArticleMapper articleMapper;
    private final VisitLogMapper visitLogMapper;
    private final SiteConfigMapper siteConfigMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public SiteConfigVO getSiteConfig() {
        // 先从缓存获取
        String key = Constants.REDIS_SITE_CONFIG;
        @SuppressWarnings("unchecked")
        Map<String, String> configMap = (Map<String, String>) redisTemplate.opsForValue().get(key);

        if (configMap == null) {
            List<SiteConfig> configs = getAllConfigs();
            configMap = configs.stream()
                    .collect(Collectors.toMap(SiteConfig::getConfigKey, c -> c.getConfigValue() != null ? c.getConfigValue() : ""));
            redisTemplate.opsForValue().set(key, configMap, 1, TimeUnit.HOURS);
        }

        return SiteConfigVO.fromMap(configMap);
    }

    @Override
    public void updateSiteConfig(Map<String, String> configMap) {
        configMap.forEach((key, value) -> {
            SiteConfig config = new SiteConfig();
            config.setConfigKey(key);
            config.setConfigValue(value);
            updateConfigByKey(config);
        });

        // 清除缓存
        redisTemplate.delete(Constants.REDIS_SITE_CONFIG);
    }

    @Override
    public DashboardStatsResponse getDashboardStats() {
        LocalDate today = LocalDate.now();

        // 今日 PV/UV
        Long todayPv = visitLogMapper.selectPvByDate(today);
        Long todayUv = visitLogMapper.selectUvByDate(today);

        // 文章/分类/评论数量
        Long articleCount = articleRepository.countTotal(Constants.ARTICLE_STATUS_PUBLISHED);
        Long categoryCount = categoryRepository.countTotal();
        Long commentCount = commentRepository.countTotal(Constants.COMMENT_STATUS_APPROVED);

        // 热门文章
        List<Article> hotArticles = articleRepository.findHotArticles(10, true, null);
        List<ArticleVO> hotArticleVOs = hotArticles.stream()
                .map(ArticleVO::fromEntity)
                .collect(Collectors.toList());

        // 近7日 PV 趋势
        LocalDate startDate = today.minusDays(6);
        List<Long> pvTrend = visitLogMapper.selectPvTrend(startDate, today);

        return new DashboardStatsResponse(
                todayPv != null ? todayPv : 0L,
                todayUv != null ? todayUv : 0L,
                articleCount != null ? articleCount : 0L,
                categoryCount != null ? categoryCount : 0L,
                commentCount != null ? commentCount : 0L,
                hotArticleVOs,
                pvTrend
        );
    }

    @Override
    public void recordVisit(String ip, String url, Long articleId, String userAgent, String referer) {
        VisitLog log = new VisitLog();
        log.setIp(ip);
        log.setUrl(url);
        log.setArticleId(articleId);
        log.setUserAgent(userAgent);
        log.setReferer(referer);
        log.setVisitDate(LocalDate.now());
        visitLogMapper.insert(log);
    }

    private List<SiteConfig> getAllConfigs() {
        return siteConfigMapper.selectAll();
    }

    private void updateConfigByKey(SiteConfig config) {
        siteConfigMapper.updateByKey(config);
    }
}
