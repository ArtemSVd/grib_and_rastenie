package com.example.gribyandrasteniyamap.databse;

import android.content.Context;

import androidx.room.Room;

import javax.inject.Provider;
import javax.inject.Singleton;

import toothpick.ProvidesSingletonInScope;

@Singleton
@ProvidesSingletonInScope
public class DatabaseProvider implements Provider<AppDatabase> {

    private final Context context;

    public DatabaseProvider(Context context) {
        this.context = context;
    }

    @Override
    public AppDatabase get() {
        return Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "database")
                .build();
    }
}
