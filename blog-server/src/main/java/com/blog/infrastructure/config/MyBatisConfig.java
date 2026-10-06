package com.blog.infrastructure.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis配置类
 */
@Configuration
@MapperScan("com.blog.infrastructure.persistence.mapper")
public class MyBatisConfig {
}
