package com.xbot.security;

import cn.hutool.json.JSONUtil;
import com.xbot.common.Result;
import com.xbot.common.ResultCode;
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
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
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

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        JwtUtils jwtUtils = new JwtUtils(jwtProperties);
        // 1. 获取请求路径
        String path = request.getServletPath();
        log.debug("Processing request for path: {}", path);

        // 2. 提取 Header
        String headerName = jwtProperties.getHeader();
        String prefix = jwtProperties.getPrefix();
        String authHeader = request.getHeader(headerName);

        // 3. 校验 Token 格式
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith(prefix)) {
            chain.doFilter(request, response);
            return;
        }
        String token = authHeader.substring(prefix.length());

        try {
            // 4. 使用 Hutool 校验 Token 有效性（包括过期时间、签名）
            if (jwtUtils.verify(token)) {
                String username = jwtUtils.getUsername(token);
                String role = jwtUtils.getRole(token);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // 构建 Authority 对象 (必须以 ROLE_ 开头)
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            username, null, Collections.singletonList(authority));

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 设置认证信息到上下文
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.info("Authenticated user: {}, with role: {}", username, role);
                }
            } else {
                // Token 验证失败（过期或篡改）
                handleException(response, "登录凭证已过期或无效，请重新登录");
                return;
            }
        } catch (Exception e) {
            log.error("JWT Authentication failed: {}", e.getMessage());
            handleException(response, "认证解析异常");
            return;
        }

        chain.doFilter(request, response);
    }

    /**
     * Filter 内部发生的异常，手动构建 JSON 响应给前端
     */
    private void handleException(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // 使用通用返回类封装错误信息
        Result<Object> result = Result.failed(ResultCode.UNAUTHORIZED, message);

        // 使用 Hutool 将对象转为 JSON 字符串
        response.getWriter().write(JSONUtil.toJsonStr(result));
    }
}