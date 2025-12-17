package com.xbot.config;

import com.xbot.common.Result;
import com.xbot.security.TokenAuthFilter;
import cn.hutool.json.JSONUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity // 必须开启
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenAuthFilter tokenAuthFilter;

    // 定义白名单路径
    private static final String[] WHITE_LIST = {
            "/auth/login",
            "/auth/logout",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/webjars/**",
            "/doc.html"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. 禁用 CSRF 并开启 CORS
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 2. 无状态 Session
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 3. 异常处理：利用我们之前写的 Result 通用返回类
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, resp, e) -> writeError(resp, 401, "未登录或登录已过期"))
                        .accessDeniedHandler((req, resp, e) -> writeError(resp, 403, "权限不足，拒绝访问"))
                )

                // 4. 权限配置
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(WHITE_LIST).permitAll() // 白名单放行
                        .requestMatchers("/admin/**").hasRole("ADMIN") // 角色控制
                        .anyRequest().authenticated() // 剩下全拦截
                )

                // 5. 插入自定义过滤器
                .addFilterBefore(tokenAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * 抽取：将异常信息通过 Result 类渲染为 JSON
     */
    private void writeError(HttpServletResponse resp, int code, String msg) throws java.io.IOException {
        resp.setStatus(code);
        resp.setContentType(MediaType.APPLICATION_JSON_VALUE);
        resp.setCharacterEncoding("UTF-8");
        // 使用 Result.error 保持全站返回格式一致
        resp.getWriter().write(JSONUtil.toJsonStr(Result.failed(code, msg)));
    }

    /**
     * 跨域配置 Bean
     */
    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("*"); // 允许任何来源，生产环境建议指定具体域名
        config.addAllowedMethod("*");        // 允许所有 HTTP 方法
        config.addAllowedHeader("*");        // 允许所有 Header
        config.setAllowCredentials(true);    // 允许携带 Cookie/认证信息

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}