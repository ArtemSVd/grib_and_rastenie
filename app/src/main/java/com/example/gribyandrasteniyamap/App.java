package com.example.gribyandrasteniyamap;


import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;

import com.example.gribyandrasteniyamap.module.AdapterModule;
import com.example.gribyandrasteniyamap.module.DatabaseModule;
import com.example.gribyandrasteniyamap.module.ServiceModule;
import com.example.gribyandrasteniyamap.module.UserModule;
import com.example.gribyandrasteniyamap.service.ServerScheduler;
import com.example.gribyandrasteniyamap.view.model.User;

import javax.inject.Inject;

import toothpick.Scope;
import toothpick.Toothpick;

public final class App extends Application {
    public static final String APP_PREFERENCES = "SETTINGS";
    public static final String APP_PREFERENCES_USER = "USER";

    public static SharedPreferences sharedPreferences;

    @Inject
    ServerScheduler scheduler;

    @Override
    public void onCreate() {
        super.onCreate();
        Scope appScope = Toothpick.openScope("APP");

        sharedPreferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        String name = sharedPreferences.contains(APP_PREFERENCES_USER) ? sharedPreferences.getString(APP_PREFERENCES_USER, "") : null;
        String deviceName = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        User user = User.builder()
                .name(name)
                .deviceName(deviceName)
                .build();

        appScope.installModules(new DatabaseModule(getApplicationContext()),
                new ServiceModule(),
                new AdapterModule(),
                new UserModule(user)
        );

        Toothpick.inject(this, Toothpick.openScope("APP"));

        scheduler.run();
    }
}
