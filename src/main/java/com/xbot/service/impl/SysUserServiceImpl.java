package com.xbot.service.impl;


import com.xbot.common.Result;
import com.xbot.entity.SysUser;
import com.xbot.repository.UserRepository;
import com.xbot.security.JwtUtils;
import com.xbot.service.SysUserService;
import jakarta.annotation.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceImpl implements SysUserService {

    @Resource
    private UserRepository userRepository;

    private final PasswordEncoder passwordEncoder; // 注入密码加密器
    private final JwtUtils jwtUtils;

    public SysUserServiceImpl(PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public String login(String username, String password) {
        // 2. 查找用户
        SysUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("账号不存在"));

        // 3. 校验密码 (使用加密比对)
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        // 4. 生成真正的 JWT Token
        // 根据实体中的 role 定义 (0:ADMIN, 1:USER) 转换为 Spring Security 识别的角色名
        String roleName = user.getRole() == 0 ? "ROLE_ADMIN" : "ROLE_USER";

        return jwtUtils.createToken(user.getUsername(), roleName);
    }
}
