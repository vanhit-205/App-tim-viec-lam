package com.example.timviecapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * TokenManager - Quản lý token và thông tin user đăng nhập
 * Sử dụng SharedPreferences để lưu trữ persistent
 * Token không bị mất khi kill app
 */
public class TokenManager {
    private static final String PREF_NAME = "workhub_prefs";
    private static final String KEY_TOKEN = "access_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_ROLE = "user_role";

    private static SharedPreferences prefs;

    /**
     * Khởi tạo TokenManager - gọi trong Application.onCreate()
     */
    public static void init(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // ========== Token Methods ==========

    public static void saveToken(String accessToken) {
        if (prefs != null) {
            prefs.edit().putString(KEY_TOKEN, accessToken).apply();
        }
    }

    public static String getToken() {
        return prefs != null ? prefs.getString(KEY_TOKEN, null) : null;
    }

    public static void saveRefreshToken(String refreshToken) {
        if (prefs != null) {
            prefs.edit().putString(KEY_REFRESH_TOKEN, refreshToken).apply();
        }
    }

    public static String getRefreshToken() {
        return prefs != null ? prefs.getString(KEY_REFRESH_TOKEN, null) : null;
    }

    // ========== User Info Methods ==========

    /**
     * Lưu toàn bộ thông tin user sau khi đăng nhập thành công
     */
    public static void saveUserInfo(int userId, String email, String name, String role) {
        if (prefs != null) {
            prefs.edit()
                    .putInt(KEY_USER_ID, userId)
                    .putString(KEY_USER_EMAIL, email)
                    .putString(KEY_USER_NAME, name)
                    .putString(KEY_USER_ROLE, role)
                    .apply();
        }
    }

    public static int getUserId() {
        return prefs != null ? prefs.getInt(KEY_USER_ID, -1) : -1;
    }

    public static String getUserEmail() {
        return prefs != null ? prefs.getString(KEY_USER_EMAIL, null) : null;
    }

    public static String getUserName() {
        return prefs != null ? prefs.getString(KEY_USER_NAME, null) : null;
    }

    public static String getUserRole() {
        return prefs != null ? prefs.getString(KEY_USER_ROLE, null) : null;
    }

    // ========== Session Methods ==========

    /**
     * Kiểm tra user đã đăng nhập hay chưa
     */
    public static boolean isLoggedIn() {
        String token = getToken();
        return token != null && !token.isEmpty();
    }

    /**
     * Xóa toàn bộ dữ liệu khi đăng xuất
     */
    public static void clear() {
        if (prefs != null) {
            prefs.edit().clear().apply();
        }
    }
}
