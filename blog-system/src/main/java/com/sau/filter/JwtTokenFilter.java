package com.sau.filter;

import com.sau.constants.JwtConstants;
import com.sau.constants.RequestConstants;
import com.sau.constants.ResponseConstants;
import com.sau.constants.SecurityWhitelist;
import com.sau.exception.TokenInvalidException;
import com.sau.security.AuthUser;
import com.sau.service.AuthUserService;
import com.sau.utils.CurrentHolderUtils;
import com.sau.utils.JwtUtils;
import com.sau.utils.RedisTokenUtils;
import com.sau.utils.ResponseUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 认证过滤器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
    private final RedisTokenUtils redisTokenUtils;
    private final AuthUserService authUserService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // 白名单接口直接放行，不进入鉴权流程
            if (isPublicEndpoint(request)) {
                filterChain.doFilter(request, response);
                return;
            }

            // 尝试从请求头提取 accessToken
            String token = resolveAccessToken(request);
            if (token == null) {
                filterChain.doFilter(request, response);
                return;
            }

            // 解析用户身份并校验 Redis 中的 accessToken 是否有效
            Integer userId = parseUserId(token);
            if (!isAccessTokenValid(userId, token)) {
                writeUnauthorized(response);
                return;
            }

            // 重新加载认证用户信息并写入 Security 上下文
            AuthUser authUser = authUserService.loadAuthUser(userId);
            if (authUser == null) {
                writeUnauthorized(response);
                return;
            }

            storeAuthentication(authUser);
            filterChain.doFilter(request, response);
        } catch (TokenInvalidException e) {
            log.error("认证失败", e);
            writeUnauthorized(response);
        } finally {
            // 请求结束后清理线程变量中的当前用户
            CurrentHolderUtils.remove();
        }
    }

    /**
     * 判断当前请求是否属于公开接口
     */
    private boolean isPublicEndpoint(HttpServletRequest request) {
        return SecurityWhitelist.isPublicEndpoint(request.getMethod(), request.getRequestURI(), antPathMatcher);
    }

    /**
     * 从请求头中提取 accessToken
     */
    private String resolveAccessToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(RequestConstants.AUTHORIZATION);
        if (authorizationHeader == null || !authorizationHeader.startsWith(RequestConstants.BEARER)) {
            return null;
        }
        String token = authorizationHeader.substring(RequestConstants.BEARER.length());
        return token.isEmpty() ? null : token;
    }

    /**
     * 解析 token 中的用户 ID
     */
    private Integer parseUserId(String token) {
        Claims claims = JwtUtils.parseToken(token);
        return Integer.valueOf(claims.get(JwtConstants.USER_ID).toString());
    }

    /**
     * 校验 accessToken 是否仍然有效
     */
    private boolean isAccessTokenValid(Integer userId, String token) {
        String storedAccessToken = redisTokenUtils.getAccessToken(userId);
        return storedAccessToken != null && token.equals(storedAccessToken);
    }

    /**
     * 将当前认证用户写入上下文
     */
    private void storeAuthentication(AuthUser authUser) {
        CurrentHolderUtils.setCurrentUser(authUser);
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(authUser, null, authUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    /**
     * 返回统一的未认证响应
     */
    private void writeUnauthorized(HttpServletResponse response) throws IOException {
        ResponseUtils.writeResponse(
                response,
                ResponseConstants.JSON_UTF8,
                HttpServletResponse.SC_UNAUTHORIZED,
                ResponseConstants.UNAUTHORIZED);
    }
}
