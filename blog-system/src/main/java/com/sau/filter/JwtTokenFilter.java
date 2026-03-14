package com.sau.filter;

import com.sau.constants.JwtConstants;
import com.sau.constants.RequestConstants;
import com.sau.constants.ResponseConstants;
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
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final RedisTokenUtils redisTokenUtils;
    private final AuthUserService authUserService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = resolveAccessToken(request);
            if (token == null) {
                filterChain.doFilter(request, response);
                return;
            }

            Integer userId = parseUserId(token);
            if (!isAccessTokenValid(userId, token)) {
                writeUnauthorized(response);
                return;
            }

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
            CurrentHolderUtils.remove();
        }
    }

    private String resolveAccessToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(RequestConstants.AUTHORIZATION);
        if (authorizationHeader == null || !authorizationHeader.startsWith(RequestConstants.BEARER)) {
            return null;
        }
        String token = authorizationHeader.substring(RequestConstants.BEARER.length());
        return token.isEmpty() ? null : token;
    }

    private Integer parseUserId(String token) {
        Claims claims = JwtUtils.parseToken(token);
        return Integer.valueOf(claims.get(JwtConstants.USER_ID).toString());
    }

    private boolean isAccessTokenValid(Integer userId, String token) {
        String storedAccessToken = redisTokenUtils.getAccessToken(userId);
        return storedAccessToken != null && token.equals(storedAccessToken);
    }

    private void storeAuthentication(AuthUser authUser) {
        CurrentHolderUtils.setCurrentUser(authUser);
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(authUser, null, authUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    private void writeUnauthorized(HttpServletResponse response) throws IOException {
        ResponseUtils.writeResponse(
                response,
                ResponseConstants.JSON_UTF8,
                HttpServletResponse.SC_UNAUTHORIZED,
                ResponseConstants.UNAUTHORIZED);
    }
}
