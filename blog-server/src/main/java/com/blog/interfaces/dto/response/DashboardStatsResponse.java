package com.blog.interfaces.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 仪表盘统计响应
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponse {
    private Long todayPv;
    private Long todayUv;
    private Long articleCount;
    private Long categoryCount;
    private Long commentCount;
    private List<ArticleVO> hotArticles;
    private List<Long> pvTrend;
}
