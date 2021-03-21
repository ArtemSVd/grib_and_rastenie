package com.example.gribyandrasteniyamap;

import android.annotation.SuppressLint;
import android.app.Application;
import android.provider.Settings;

import com.example.gribyandrasteniyamap.module.AdapterModule;
import com.example.gribyandrasteniyamap.module.DatabaseModule;
import com.example.gribyandrasteniyamap.module.ServiceModule;
import com.example.gribyandrasteniyamap.module.SharedPreferencesModule;
import com.example.gribyandrasteniyamap.module.UserModule;
import com.example.gribyandrasteniyamap.service.SharedPreferencesService;
import com.example.gribyandrasteniyamap.view.model.User;

import javax.inject.Inject;

import toothpick.Scope;
import toothpick.Toothpick;

import static com.example.gribyandrasteniyamap.service.SharedPreferencesService.USERNAME;

public final class App extends Application {

    @Inject
    SharedPreferencesService sharedPreferencesService;

    @Override
    public void onCreate() {
        super.onCreate();
        Scope appScope = Toothpick.openScope("APP");

        appScope.installModules(new SharedPreferencesModule(getApplicationContext()));

        Toothpick.inject(this, Toothpick.openScope("APP"));

        String name = sharedPreferencesService.getStringValueByKey(USERNAME);
        @SuppressLint("HardwareIds") String deviceName = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        User user = User.builder()
                .name(name)
                .deviceName(deviceName)
                .build();

        appScope.installModules(
                new DatabaseModule(),
                new ServiceModule(),
                new AdapterModule(),
                new UserModule(user)
        );
    }
}
