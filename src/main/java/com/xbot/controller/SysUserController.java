package com.xbot.controller;

import com.xbot.entity.SysUser;
import com.xbot.repository.UserRepository;
import com.xbot.security.TokenStore;
import com.xbot.security.TokenUtil;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
public class SysUserController {

    @Resource
    private UserRepository userRepository;

    @PostMapping("/auth/login")
    public Map<String, Object> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        Optional<SysUser> sysUser = userRepository.findByUsername(username);
        if (sysUser.isEmpty()) {
            return Map.of("code", "-1","msg","账号不存在");
        }
        if (sysUser.get().getPassword().equals(password)) {
            return Map.of("ok", false, "msg", "账号或密码错误");
        }

        String token = TokenUtil.generateToken();
        TokenStore.save(token, username);

        return Map.of("ok", true, "token", token);
    }


    @GetMapping("/admin")
    public Map<String, Object> admin() {
        return Map.of("ok", true, "msg", "你是 ADMIN");
    }

}
