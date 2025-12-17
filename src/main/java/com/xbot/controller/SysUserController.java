package com.xbot.controller;

import com.xbot.common.Result;
import com.xbot.dto.LoginDTO;
import com.xbot.entity.SysUser;
import com.xbot.repository.UserRepository;
import com.xbot.security.JwtUtils;
import jakarta.annotation.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController("/auth")
public class SysUserController {

    @Resource
    private UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // 注入密码加密器

    public SysUserController(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public Result<String> login(@Validated @RequestBody LoginDTO loginDTO) {
        // 1. 校验验证码 (建议在 DTO 中接收 uuid)
        // captchaService.validate(loginDTO.getUuid(), loginDTO.getCaptchaCode());

        // 2. 查找用户
        SysUser user = userRepository.findByUsername(loginDTO.getUsername())
                .orElseThrow(() -> new RuntimeException("账号不存在"));

        // 3. 校验密码 (使用加密比对)
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            return Result.failed("账号或密码错误");
        }
        // 4. 生成真正的 JWT Token
        // 根据实体中的 role 定义 (0:ADMIN, 1:USER) 转换为 Spring Security 识别的角色名
        String roleName = user.getRole() == 0 ? "ROLE_ADMIN" : "ROLE_USER";

        JwtUtils jwtUtils = new JwtUtils();
        String token = jwtUtils.createToken(user.getUsername(), roleName);

        return Result.success(token);
    }


    @GetMapping("/admin")
    public Map<String, Object> admin() {
        return Map.of("ok", true, "msg", "你是 ADMIN");
    }

}
