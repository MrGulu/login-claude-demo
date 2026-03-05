package com.demo.login.common.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * JWT工具类
 *
 * @author Claude
 * @since 2024-03-02
 */
@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expire}")
    private Long expire;

    /**
     * 生成Token
     *
     * @param userId 用户ID
     * @param username 用户名
     * @return Token字符串
     */
    public String generateToken(Long userId, String username) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            Date expireDate = new Date(System.currentTimeMillis() + expire * 1000);

            return JWT.create()
                    .withClaim("userId", userId)
                    .withClaim("username", username)
                    .withIssuedAt(new Date())
                    .withExpiresAt(expireDate)
                    .sign(algorithm);
        } catch (Exception e) {
            log.error("生成Token失败", e);
            return null;
        }
    }

    /**
     * 验证Token
     *
     * @param token Token字符串
     * @return 是否有效
     */
    public boolean verifyToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm).build();
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException e) {
            log.error("Token验证失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 解析Token
     *
     * @param token Token字符串
     * @return 解码后的JWT
     */
    public DecodedJWT parseToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm).build();
            return verifier.verify(token);
        } catch (JWTVerificationException e) {
            log.error("Token解析失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 从Token中获取用户ID
     *
     * @param token Token字符串
     * @return 用户ID
     */
    public Long getUserIdFromToken(String token) {
        DecodedJWT jwt = parseToken(token);
        return jwt != null ? jwt.getClaim("userId").asLong() : null;
    }

    /**
     * 从Token中获取用户名
     *
     * @param token Token字符串
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        DecodedJWT jwt = parseToken(token);
        return jwt != null ? jwt.getClaim("username").asString() : null;
    }

    /**
     * 判断Token是否过期
     *
     * @param token Token字符串
     * @return 是否过期
     */
    public boolean isTokenExpired(String token) {
        DecodedJWT jwt = parseToken(token);
        if (jwt == null) {
            return true;
        }
        Date expiresAt = jwt.getExpiresAt();
        return expiresAt.before(new Date());
    }
}
