package com.example.gribyandrasteniyamap.module;

import com.example.gribyandrasteniyamap.databse.AppDatabase;
import com.example.gribyandrasteniyamap.databse.DatabaseProvider;

import toothpick.config.Module;

public class DatabaseModule extends Module {
    public DatabaseModule() {
        bind(AppDatabase.class).toProviderInstance(new DatabaseProvider()).providesSingleton();
    }
}
