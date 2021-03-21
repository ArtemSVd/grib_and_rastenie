package com.example.gribyandrasteniyamap.module;

import android.content.Context;

import com.example.gribyandrasteniyamap.service.SharedPreferencesService;

import toothpick.config.Module;

public class SharedPreferencesModule extends Module {
    public SharedPreferencesModule(Context context) {
        bind(Context.class).toInstance(context);
        bind(SharedPreferencesService.class);
    }
}
