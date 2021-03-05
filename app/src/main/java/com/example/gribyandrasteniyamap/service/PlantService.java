package com.example.gribyandrasteniyamap.service;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.gribyandrasteniyamap.databse.AppDatabase;
import com.example.gribyandrasteniyamap.databse.entity.Coordinate;
import com.example.gribyandrasteniyamap.databse.entity.Plant;
import com.example.gribyandrasteniyamap.enums.KingdomType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class PlantService {
    @Inject
    AppDatabase appDatabase;

    @Inject
    public PlantService() {
    }

    public long insertNewPlant(String filePath) {
        Plant plant = Plant.builder()
                .filePath(filePath)
                .type(KingdomType.NO)
                .build();
        return appDatabase.plantDao().insert(plant);
    }

    public Plant getById(long id) {
        return appDatabase.plantDao().getById(id);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<Plant> getAll() {
        List<Plant> plants = appDatabase.plantDao().getAll();

        // todo: подумать как сделать проще
        List<Plant> copy = new ArrayList<>(plants);
        copy.forEach(p -> {
            File file = new File(p.getFilePath());
            if (!file.exists()) {
                plants.remove(p);
                delete(p);
            }
        });

        return plants;
    }

    public int update(Plant plant) {
        return appDatabase.plantDao().update(plant);
    }

    public int delete(Plant plant) {
        return appDatabase.plantDao().delete(plant);
    }
}
