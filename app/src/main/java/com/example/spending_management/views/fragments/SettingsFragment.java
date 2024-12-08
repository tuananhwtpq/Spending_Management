package com.example.spending_management.views.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.example.spending_management.R;

import java.util.Locale;


public class SettingsFragment extends Fragment {

    private Switch switchDarkMode;
    private TextView tvLanguage;


    private boolean check = true;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_settings, container, false);

        // Initialize views
        switchDarkMode = view.findViewById(R.id.switch_dark_mode);
        tvLanguage = view.findViewById(R.id.tv_current_language);


        // Load saved preferences
        SharedPreferences preferences = requireActivity().getSharedPreferences("settings", getContext().MODE_PRIVATE);
        boolean isDarkMode = preferences.getBoolean("dark_mode", false);
        String currentLanguage = preferences.getString("language", "English");

        // Set the initial state of UI components
        switchDarkMode.setChecked(isDarkMode);
        tvLanguage.setText(currentLanguage);
        if (check)
        {
            check = false;
            tvLanguage.setText("English");
        }


        // Handle Dark Mode Switch
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("dark_mode", isChecked);
            editor.apply();

            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );

            Toast.makeText(getContext(), "Dark Mode " + (isChecked ? "Enabled" : "Disabled"), Toast.LENGTH_SHORT).show();
        });

        // Handle Language Change
        tvLanguage.setOnClickListener(v -> {
            String[] languages = {"English", "Tiếng Việt"};
            String[] languageCodes = {"en", "vi"}; // Mã ngôn ngữ tương ứng

            new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("Select Language")
                    .setItems(languages, (dialog, which) -> {
                        String selectedLanguage = languages[which];
                        String selectedLanguageCode = languageCodes[which];

                        // Lưu ngôn ngữ đã chọn vào SharedPreferences
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("language", selectedLanguage);
                        editor.putString("language_code", selectedLanguageCode);
                        editor.apply();

                        // Cập nhật Locale
                        setLocale(selectedLanguageCode);

                        // Cập nhật TextView hiển thị ngôn ngữ
                        tvLanguage.setText(selectedLanguage);

                        // Thông báo thành công
                        Toast.makeText(getContext(), "Language set to " + selectedLanguage, Toast.LENGTH_SHORT).show();

                        // Làm mới Activity để áp dụng thay đổi (Tái tạo lại Activity và các Fragment)
                        requireActivity().recreate();
                    })
                    .show();
        });

        return view;
    }

    private void setLocale(String langCode) {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);

        // Cập nhật cấu hình ngôn ngữ
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.setLocale(locale);
        requireContext().getResources().updateConfiguration(config, requireContext().getResources().getDisplayMetrics());
    }

}