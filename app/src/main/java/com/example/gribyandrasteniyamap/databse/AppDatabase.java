package com.example.gribyandrasteniyamap.databse;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.gribyandrasteniyamap.databse.converter.DateConverter;
import com.example.gribyandrasteniyamap.databse.converter.KingdomTypeConverter;
import com.example.gribyandrasteniyamap.databse.dao.PlantDao;
import com.example.gribyandrasteniyamap.databse.entity.Plant;


@Database(entities = {Plant.class}, version = 1)
@TypeConverters({KingdomTypeConverter.class, DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract PlantDao plantDao();
}
