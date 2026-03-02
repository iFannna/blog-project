package com.sau.config;

import com.sau.constants.ResponseConstants;
import com.sau.filter.JwtTokenFilter;
import com.sau.utils.ResponseUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
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
 * Spring Security
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    // 注入JwtTokenFilter
    @Autowired
    private JwtTokenFilter jwtTokenFilter;

    /**
     * 密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /**
     * 认证管理器
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * 核心安全过滤器链配置
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. 关闭CSRF（前后端分离+JWT场景不需要CSRF保护）
                .csrf(AbstractHttpConfigurer::disable)
                // 2. 配置会话管理：无状态（JWT不依赖session）
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // 3. 配置URL授权规则
                .authorizeHttpRequests(auth -> auth
                        // 放行登录、注册接口
                        .requestMatchers("/login","/refresh-token", "/register", "/send-register-code",
                                "/articles/**", "/tags","/categories"
                                ).permitAll()

                        // 其他所有请求需要认证
                        .anyRequest().authenticated()
                )
                // 4. 配置异常处理
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            // 响应HTTP状态码和错误信息
                            ResponseUtils.writeResponse(
                                    response,
                                    ResponseConstants.JSON_UTF8,
                                    HttpServletResponse.SC_UNAUTHORIZED,
                                    ResponseConstants.UNAUTHORIZED
                            );
                        })
                )
                // 禁用Spring Security默认的logout处理，避免与自定义/logout冲突
                .logout(AbstractHttpConfigurer::disable)
                // 注册JWT过滤器
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);



        return http.build();
    }
}