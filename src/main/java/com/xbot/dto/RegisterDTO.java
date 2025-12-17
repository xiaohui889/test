package com.xbot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "用户注册参数")
public class RegisterDTO {
    @NotBlank(message = "用户名必填")
    private String username;

    @NotBlank(message = "密码必填")
    private String password;

    @Schema(description = "用户输入的验证码")
    @NotBlank(message = "验证码不能为空")
    private String captchaCode;

    private String email;
}