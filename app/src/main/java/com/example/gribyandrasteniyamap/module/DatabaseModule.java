package com.example.gribyandrasteniyamap.module;

import android.content.Context;

import com.example.gribyandrasteniyamap.databse.AppDatabase;
import com.example.gribyandrasteniyamap.databse.DatabaseProvider;

import toothpick.config.Module;

public class DatabaseModule extends Module {
    public DatabaseModule(Context context) {
        bind(AppDatabase.class).toProviderInstance(new DatabaseProvider(context)).providesSingleton();
    }
}
