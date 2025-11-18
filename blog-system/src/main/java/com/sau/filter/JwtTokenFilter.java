package com.sau.filter;

import com.sau.constants.JwtConstants;
import com.sau.constants.RequestConstants;
import com.sau.constants.ResponseConstants;
import com.sau.exception.TokenInvalidException;
import com.sau.utils.CurrentHolderUtils;
import com.sau.utils.JwtUtils;
import com.sau.utils.RedisTokenUtils;
import com.sau.utils.ResponseUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * JWT认证过滤器
 */
@Slf4j
@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    @Autowired
    private RedisTokenUtils redisTokenUtils;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException
            , IOException {

        try {
            log.info("JWT认证过滤器开始执行");
            // 1. 跳过不需要认证的接口
            List<String> publicPaths = Arrays.asList(
                    "/login", "/register", "/send-register-code", "/refresh-token",
                    "/wxCheck", "/wxLogin", "/wxCallback"
            );

            // 获取请求路径
            String uri = request.getRequestURI();
            log.info("请求路径：{}", uri);
            AntPathMatcher antPathMatcher = new AntPathMatcher();
            // 判断当前请求路径是否在白名单中 可用uri.contains("/login")判断是否包含字符串
            boolean isPublic = publicPaths.stream().anyMatch(path -> antPathMatcher.match(path, uri));
            if (isPublic) {
                filterChain.doFilter(request, response);
                return;
            }

            // 2. 从Authorization头获取并解析access token
            String authorizationHeader = request.getHeader(RequestConstants.AUTHORIZATION);
            log.info("从请求头中获取Authorization：{}", authorizationHeader);

            String token = null;
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                token = authorizationHeader.substring(7);
            }
            // 校验令牌是否为空
            if (token == null || token.isEmpty()) {
                log.warn("令牌为空");
                filterChain.doFilter(request, response);
                return;
            }

            // 3. 解析accessToken
            Claims claims = JwtUtils.parseToken(token);
            log.info("解析成功：claims:{}", claims);
            Integer userId = Integer.valueOf(claims.get(JwtConstants.USER_ID).toString());

            // 4. 校验accessToken是否在Redis中
            if (redisTokenUtils.getAccessToken(userId) == null) {
                log.warn("当前accessToken未在Redis中,认证校验失败");
                // 返回401状态码
                ResponseUtils.writeResponse(
                        response,
                        ResponseConstants.JSON_UTF8,
                        HttpServletResponse.SC_UNAUTHORIZED,
                        ResponseConstants.UNAUTHORIZED
                );
                return;
            }

            // 5. 登录成功后将当前用户ID保存在ThreadLocal中
            log.info("将当前用户ID保存在ThreadLocal中：{}", userId);
            CurrentHolderUtils.setCurrentId(userId);

            // 6. JwtTokenFilter放行后需要经过Spring Security的AuthenticationManager放行
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(auth);
            log.info("校验成功");

            // 7. 放行请求
            filterChain.doFilter(request, response);

        } catch (TokenInvalidException e) {
            // 解析失败（过期/签名错误），返回401状态码
            log.error("认证失败：{}", e.getMessage());

            ResponseUtils.writeResponse(
                    response,
                    ResponseConstants.JSON_UTF8,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    ResponseConstants.UNAUTHORIZED
            );
        } finally {
            CurrentHolderUtils.remove();
        }
    }
}