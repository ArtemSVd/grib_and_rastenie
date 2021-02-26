package com.example.gribyandrasteniyamap.service;

import com.example.gribyandrasteniyamap.databse.AppDatabase;
import com.example.gribyandrasteniyamap.databse.entity.Coordinate;
import com.example.gribyandrasteniyamap.databse.entity.Plant;
import com.example.gribyandrasteniyamap.enums.KingdomType;

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
                //todo: получить данные геолокации
                .coordinate(Coordinate.builder()
                        .latitude("46.5235")
                        .longitude("54.1231")
                        .build())
                .build();
        return appDatabase.plantDao().insert(plant);
    }

    public Plant getById(long id) {
        return appDatabase.plantDao().getById(id);
    }

    public List<Plant> getAll() {
        return appDatabase.plantDao().getAll();
    }

    public int update(Plant plant) {
        return appDatabase.plantDao().update(plant);
    }

    public int delete(Plant plant) {
        return appDatabase.plantDao().delete(plant);
    }
}
