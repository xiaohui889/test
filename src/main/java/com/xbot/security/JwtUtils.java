package com.xbot.security;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.signers.JWTSignerUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component // 标记为组件
@RequiredArgsConstructor
public class JwtUtils {

    private final JwtProperties jwtProperties;

    public String createToken(String username, String role) {
        byte[] key = jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8);
        return JWT.create()
                .setPayload("username", username)
                .setPayload("role", role)
                .setExpiresAt(new Date(System.currentTimeMillis() + jwtProperties.getExpiration()))
                .setSigner(JWTSignerUtil.hs256(key))
                .sign();
    }

    public boolean verify(String token) {
        try {
            byte[] key = jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8);
            return JWTUtil.verify(token, key);
        } catch (Exception e) {
            return false;
        }
    }

    public String getUsername(String token) {
        return (String) JWTUtil.parseToken(token).getPayload("username");
    }

    public String getRole(String token) {
        return (String) JWTUtil.parseToken(token).getPayload("role");
    }

    // 获取前缀（比如给前端展示或 Filter 使用）
    public String getPrefix() {
        return jwtProperties.getPrefix();
    }
}
