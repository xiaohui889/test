package com.xbot.controller;

import com.xbot.common.Result;
import com.xbot.dto.LoginDTO;
import com.xbot.entity.SysUser;
import com.xbot.repository.UserRepository;
import com.xbot.security.JwtUtils;
import com.xbot.service.SysUserService;
import jakarta.annotation.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class SysUserController {
    
    private final SysUserService sysUserService;

    public SysUserController(SysUserService sysUserService) {
        this.sysUserService = sysUserService;
    }
    
    @PostMapping("login")
    public Result<String> login(@Validated @RequestBody LoginDTO loginDTO) {
        // 1. 校验验证码 (建议在 DTO 中接收 uuid)
        // captchaService.validate(loginDTO.getUuid(), loginDTO.getCaptchaCode());
        String token = sysUserService.login(loginDTO.getUsername(), loginDTO.getPassword());
        return Result.success(token,"登陆成功");
    }


    @GetMapping("/admin")
    public Map<String, Object> admin() {
        return Map.of("ok", true, "msg", "你是 ADMIN");
    }

}
