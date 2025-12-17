package com.xbot.security;

import cn.hutool.json.JSONUtil;
import com.xbot.common.Result;
import com.xbot.common.ResultCode;
import com.xbot.common.SecurityConstants;
import com.xbot.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * 生产级别完善的 JWT 认证过滤器
 */
@Slf4j
@Component
@RequiredArgsConstructor // Lombok 自动注入 final 字段
public class TokenAuthFilter extends OncePerRequestFilter {

    private final JwtProperties jwtProperties;
    private final JwtUtils jwtUtils; // 注入单例

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        // 如果请求路径在白名单中，则此过滤器不执行校验逻辑
        return Arrays.stream(SecurityConstants.WHITE_LIST)
                .anyMatch(pattern -> new AntPathMatcher().match(pattern, path));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String authHeader = request.getHeader(jwtProperties.getHeader());
        String prefix = jwtProperties.getPrefix();

        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith(prefix)) {
            chain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(prefix.length());
        try {
            if (jwtUtils.verify(token)) {
                String username = jwtUtils.getUsername(token);
                String role = jwtUtils.getRole(token);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // 确保角色带上 ROLE_ 前缀以匹配 hasRole("ADMIN")
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            username, null, Collections.singletonList(authority));
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception e) {
            log.error("JWT 认证失败: {}", e.getMessage());
        }
        chain.doFilter(request, response);
    }
}