package com.xbot.common;

public class SecurityConstants {
    // 登录路径
    public static final String LOGIN_URL = "/auth/login";

    // 白名单路径数组
    public static final String[] WHITE_LIST = {
            LOGIN_URL,
            "/auth/logout",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/webjars/**",
            "/doc.html"
    };
}