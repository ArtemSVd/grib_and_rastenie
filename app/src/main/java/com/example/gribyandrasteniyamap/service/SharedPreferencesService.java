package com.example.gribyandrasteniyamap.service;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import javax.inject.Inject;
import javax.inject.Singleton;

import toothpick.Toothpick;

@Singleton
public class SharedPreferencesService {

    @Inject
    Context context;

    private final SharedPreferences sharedPreferences;

    @Inject
    public SharedPreferencesService() {
        Toothpick.inject(this, Toothpick.openScope("APP"));
        sharedPreferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    private final String APP_PREFERENCES = "SETTINGS";

    public static final String USERNAME = "user";
    public static final String UUID = "uuid";
    public static final String ENABLE_SCHEDULER = "enableScheduler";

    @Nullable
    public String getStringValueByKey(String key) {
        return sharedPreferences.contains(key) ? sharedPreferences.getString(key, "") : null;
    }

    public boolean getBooleanValueByKey(String key) {
        return sharedPreferences.contains(key) && sharedPreferences.getBoolean(key, false);
    }

    public void setValue(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void setValue(String key, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

}
