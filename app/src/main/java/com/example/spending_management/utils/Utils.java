package com.example.spending_management.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class Utils {

    public static String getCurrentLanguage(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        return preferences.getString("language_code", "en"); // "en" là giá trị mặc định nếu không tìm thấy
    }
}
