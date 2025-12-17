package com.xbot.security;

import cn.hutool.core.util.IdUtil;

public class TokenUtil {

    public static String generateToken() {
        // 简单、安全、够用
        return IdUtil.fastUUID();
    }

}
