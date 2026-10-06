package com.blog.infrastructure.persistence.mapper;

import com.blog.domain.entity.SiteConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 网站配置Mapper
 */
@Mapper
public interface SiteConfigMapper {

    SiteConfig selectByKey(@Param("configKey") String configKey);

    List<SiteConfig> selectAll();

    int updateByKey(SiteConfig config);
}
