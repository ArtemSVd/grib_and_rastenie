package com.example.gribyandrasteniyamap;


import android.app.Application;

import toothpick.Scope;
import toothpick.Toothpick;

public final class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Scope appScope = Toothpick.openScope("APP");
    }
}
