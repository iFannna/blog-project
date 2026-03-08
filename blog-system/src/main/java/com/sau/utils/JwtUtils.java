package com.sau.utils;

import com.sau.exception.TokenInvalidException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.Map;

/**
 * JWT 工具类。
 */
@Slf4j
public final class JwtUtils {

    private static final String SECRET_KEY = "U0F1NTIw";

    private JwtUtils() {
    }

    /**
     * 生成 JWT 令牌。
     */
    public static String generateToken(Map<String, Object> claims, long expirationTime) {
        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .addClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .compact();
    }

    /**
     * 解析 JWT 令牌。
     */
    public static Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            throw new TokenInvalidException("未登录或令牌无效");
        }
    }
}