package com.xbot.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class BCryptUtil {

    // strength：计算强度(4~31)，默认 10。越大越安全但越慢。
    private static final PasswordEncoder ENCODER = new BCryptPasswordEncoder(10);

    private BCryptUtil() {}

    /** 明文加密 */
    public static String hash(String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("rawPassword is blank");
        }
        return ENCODER.encode(rawPassword);
    }

    /** 校验密码 */
    public static boolean verify(String rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }
        return ENCODER.matches(rawPassword, encodedPassword);
    }

}