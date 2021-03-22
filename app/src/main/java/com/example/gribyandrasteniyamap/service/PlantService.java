package com.example.gribyandrasteniyamap.service;

import com.example.gribyandrasteniyamap.databse.AppDatabase;
import com.example.gribyandrasteniyamap.databse.entity.Coordinate;
import com.example.gribyandrasteniyamap.databse.entity.Plant;
import com.example.gribyandrasteniyamap.dto.PlantDto;
import com.example.gribyandrasteniyamap.dto.PlantsRequestParams;
import com.example.gribyandrasteniyamap.enums.KingdomType;
import com.example.gribyandrasteniyamap.service.http.PlantsAppClient;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class PlantService {

    @Inject
    AppDatabase appDatabase;

    @Inject
    PlantsAppClient httpClient;

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

    public int update(Plant plant) {
        return appDatabase.plantDao().update(plant);
    }

    public int delete(Plant plant) {
        File file = new File(plant.getFilePath());
        if (file.exists()) {
            file.delete();
        }
        return appDatabase.plantDao().delete(plant);
    }

    public int getNotSyncCount() {
        return appDatabase.plantDao().getNotSyncCount();
    }

    public List<PlantDto> getPlantsForSync(int limit) {
        return appDatabase.plantDao().getPlants(limit, false)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<PlantDto> getPlantsFromDb(PlantsRequestParams params, boolean onlyLocal) throws IOException {
        List<String> types = params.getKingdomTypes().stream().map(KingdomType::name).collect(Collectors.toList());

        List<Plant> plants;
        if (Boolean.TRUE.equals(httpClient.checkAvailable()) && !onlyLocal) {
            plants = appDatabase.plantDao().getPlants(params.getName(), types, false);
        } else {
            plants = appDatabase.plantDao().getPlants(params.getName(), types);
        }

        return plants.stream()
                .filter(plant -> plant.getCoordinate() != null)
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private PlantDto mapToDto(Plant plant) {
        return PlantDto.builder()
                .coordinate(Coordinate.builder()
                        .latitude(plant.getCoordinate().getLatitude())
                        .longitude(plant.getCoordinate().getLongitude())
                        .build())
                .name(plant.getName())
                .description(plant.getDescription())
                .filePath(plant.getFilePath())
                .type(plant.getType())
                .id(plant.getId())
                .isLocal(true)
                .build();
    }

    public List<PlantDto> getPlantsFromServer(PlantsRequestParams params) throws IOException {
        List<Integer> excludedPlantIds = appDatabase.plantDao().getExcludedPlantIds();
        params.setExcludedPlantIds(excludedPlantIds);
        return httpClient.getPlantsFromServer(params);
    }
}
