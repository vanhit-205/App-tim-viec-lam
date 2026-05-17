package com.example.timviecapp;

import android.app.Application;

import com.example.timviecapp.utils.TokenManager;

/**
 * Application class - Khởi tạo các thành phần toàn cục
 */
public class WorkHubApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Khởi tạo TokenManager với context
        TokenManager.init(this);
    }
}
