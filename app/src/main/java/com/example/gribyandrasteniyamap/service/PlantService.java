package com.example.gribyandrasteniyamap.service;

import android.annotation.SuppressLint;

import com.example.gribyandrasteniyamap.databse.AppDatabase;
import com.example.gribyandrasteniyamap.databse.entity.Coordinate;
import com.example.gribyandrasteniyamap.databse.entity.Plant;
import com.example.gribyandrasteniyamap.dto.PlantDto;
import com.example.gribyandrasteniyamap.dto.PlantsRequestParams;
import com.example.gribyandrasteniyamap.enums.KingdomType;
import com.example.gribyandrasteniyamap.service.http.HttpClient;
import com.example.gribyandrasteniyamap.utils.SerializeUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.inject.Inject;

import androidx.annotation.Nullable;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Response;

public class PlantService {

    @Inject
    AppDatabase appDatabase;

    @Inject
    HttpClient httpClient;

    private final ObjectMapper mapper = new ObjectMapper();

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
        return appDatabase.plantDao().delete(plant);
    }

    @SuppressLint("CheckResult")
    public void getPlants(PlantsRequestParams params, Consumer<List<PlantDto>> callback) {
        Observable.fromCallable(() -> getPlantsFromDb(params))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback::accept);

        Observable.fromCallable(() -> getPlantsFromServer(params))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback::accept);
    }

    private List<PlantDto> getPlantsFromDb(PlantsRequestParams params) {
        List<String> types = params.getKingdomTypes().stream().map(KingdomType::name).collect(Collectors.toList());
        return appDatabase.plantDao().getPlants(params.getName(), types)
                .stream()
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
                .type(plant.getType())
                .id(plant.getId())
                .isLocal(true)
                .build();
    }

    private List<PlantDto> getPlantsFromServer(PlantsRequestParams params) throws IOException {
        Response response = httpClient.postHttpResponse("http://172.22.206.1:8080/api/plants/list", SerializeUtil.getBytes(params));
        InputStream inputStream = getBodyResponse(response);
        if (inputStream != null) {
            return mapper.readValue(inputStream, mapper.getTypeFactory().constructCollectionType(List.class, PlantDto.class));
        }
        return Collections.emptyList();
    }

    @Nullable
    private InputStream getBodyResponse(Response response) {
        if (response != null && response.body() != null) {
            if (response.isSuccessful()) {
                return response.body().byteStream();
            }
        }
        return null;
    }
}
