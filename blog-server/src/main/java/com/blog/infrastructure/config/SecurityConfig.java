package com.blog.infrastructure.config;

import com.blog.domain.common.Constants;
import com.blog.infrastructure.security.JwtAuthenticationFilter;
import com.blog.infrastructure.security.RestAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security配置类
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RestAuthenticationEntryPoint authenticationEntryPoint;

    /**
     * 密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 安全过滤链配置
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 禁用CSRF
            .csrf(AbstractHttpConfigurer::disable)
            // 禁用Session
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 异常处理
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(authenticationEntryPoint))
            // 授权配置
            .authorizeHttpRequests(auth -> auth
                // 前台接口放行
                .requestMatchers("/api/web/**").permitAll()
                // 认证接口放行
                .requestMatchers("/api/auth/**").permitAll()
                // 后台接口按权限访问
                .requestMatchers("/api/admin/users", "/api/admin/users/**").hasAuthority(Constants.ROLE_ADMIN)
                .requestMatchers("/api/admin/**").hasAnyAuthority(Constants.ROLE_ADMIN, Constants.ROLE_EDITOR)
                // 静态资源放行
                .requestMatchers("/upload/**", "/favicon.ico").permitAll()
                // OPTIONS请求放行
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // 其他请求需要认证
                .anyRequest().authenticated()
            )
            // 添加JWT过滤器
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
