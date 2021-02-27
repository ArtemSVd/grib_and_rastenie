package com.example.gribyandrasteniyamap;


import android.app.Application;

import com.example.gribyandrasteniyamap.module.AdapterModule;
import com.example.gribyandrasteniyamap.module.DatabaseModule;
import com.example.gribyandrasteniyamap.module.ServiceModule;

import toothpick.Scope;
import toothpick.Toothpick;

public final class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Scope appScope = Toothpick.openScope("APP");
        appScope.installModules(new DatabaseModule(getApplicationContext()), new ServiceModule(), new AdapterModule());
    }
}
