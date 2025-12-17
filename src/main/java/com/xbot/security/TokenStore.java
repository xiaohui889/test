package com.xbot.security;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TokenStore {

    // token -> username
    private static final Map<String, String> TOKEN_MAP = new ConcurrentHashMap<>();

    public static void save(String token, String username) {
        TOKEN_MAP.put(token, username);
    }

    public static String getUsername(String token) {
        return TOKEN_MAP.get(token);
    }

    public static void remove(String token) {
        TOKEN_MAP.remove(token);
    }


}
