package com.xbot.service;


import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class CaptchaService {

    // 缓存容器 (key=uuid, value=code)，过期时间60秒
    private static final TimedCache<String, String> CAPTCHA_CACHE = CacheUtil.newTimedCache(120 * 1000);

    static {
        // 每5秒清理一次过期数据
        CAPTCHA_CACHE.schedulePrune(5000);
    }

    /**
     * 生成验证码
     * @return Map包含了 uuid 和 base64图片
     */
    public Map<String, String> createCaptcha() {
        // 1. 生成验证码图片 (宽120, 高40)
        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(120, 40, 4, 20);

        // 2. 生成UUID作为Key
        String uuid = UUID.randomUUID().toString();
        String code = captcha.getCode();

        // 3. 存入缓存
        CAPTCHA_CACHE.put(uuid, code);

        // 4. 返回结果
        Map<String, String> result = new HashMap<>();
        result.put("uuid", uuid);
        result.put("image", captcha.getImageBase64Data());
        return result;
    }

    /**
     * 校验验证码 (校验不通过直接抛异常，Controller就不用写if-else了)
     * @param uuid 前端传来的UUID
     * @param inputCode 前端输入的验证码
     */
    public void validate(String uuid, String inputCode) {
        // 1. 判空
        if (uuid == null || inputCode == null) {
            throw new RuntimeException("验证码参数缺失");
        }

        // 2. 从缓存取值
        String correctCode = CAPTCHA_CACHE.get(uuid);
        if (correctCode == null) {
            throw new RuntimeException("验证码已过期，请刷新");
        }

        // 3. 验证后立即移除 (防止重复使用，防暴力破解)
        CAPTCHA_CACHE.remove(uuid);

        // 4. 比对 (忽略大小写)
        if (!correctCode.equalsIgnoreCase(inputCode)) {
            throw new RuntimeException("验证码错误");
        }
    }

}
