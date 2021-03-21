package com.example.gribyandrasteniyamap.databse;

import android.content.Context;

import androidx.room.Room;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import toothpick.ProvidesSingletonInScope;
import toothpick.Toothpick;

@Singleton
@ProvidesSingletonInScope
public class DatabaseProvider implements Provider<AppDatabase> {

    @Inject
    Context context;

    public DatabaseProvider() {
        Toothpick.inject(this, Toothpick.openScope("APP"));
    }

    @Override
    public AppDatabase get() {
        return Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "database")
                .build();
    }
}
