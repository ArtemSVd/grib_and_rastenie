package com.example.gribyandrasteniyamap.service;

import android.annotation.SuppressLint;
import android.util.Log;

import com.example.gribyandrasteniyamap.databse.AppDatabase;
import com.example.gribyandrasteniyamap.databse.entity.Coordinate;
import com.example.gribyandrasteniyamap.databse.entity.Plant;
import com.example.gribyandrasteniyamap.dto.PlantDto;
import com.example.gribyandrasteniyamap.dto.PlantsRequestParams;
import com.example.gribyandrasteniyamap.enums.KingdomType;
import com.example.gribyandrasteniyamap.service.http.HttpClient;
import com.example.gribyandrasteniyamap.utils.SerializeUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
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

    public void loadOnServer() {
        Log.i("PlantService", "Загрузка данных на сервер");

        List<PlantDto> plants = appDatabase.plantDao().getPlants(5, false)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        Log.i("PlantService", plants.toString());

        if (plants.isEmpty()) {
            Log.i("PlantService", "Нечего загружать");
            return;
        }

        List<File> files = plants.stream()
                .map(plant -> getFile(plant.getFilePath()))
                .collect(Collectors.toList());

        try {
            List<Integer> loadedPlantsId = load(plants, files);
            Log.i("PlantService", "Идентификаторы загруженных сущностей: " + loadedPlantsId);
            loadedPlantsId.stream()
            .map(id -> appDatabase.plantDao().getById(id))
            .forEach(plant -> {
                plant.setSynchronized(true);
                appDatabase.plantDao().update(plant);
            });

        } catch (IOException e) {
            Log.e("PlantService", "Ошибка при загрузке данных на сервер: " + e.getMessage());
        }
    }

    @Nullable
    private File getFile(String filePath) {
        File file = new File(filePath);
        return file.exists() ? file : null;
    }

    private List<PlantDto> getPlantsFromDb(PlantsRequestParams params) throws IOException {
        List<String> types = params.getKingdomTypes().stream().map(KingdomType::name).collect(Collectors.toList());

        List<Plant> plants;
        if (Boolean.TRUE.equals(checkAvailable())) {
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

    private List<Integer> load(List<PlantDto> plants, List<File> files) throws IOException {
        Response response = httpClient.postHttpMultipartResponse("http://172.22.206.1:8080/api/plants/upload", SerializeUtil.getBytes(plants), files);
        InputStream inputStream = getBodyResponse(response);
        if (inputStream != null) {
            return mapper.readValue(inputStream, mapper.getTypeFactory().constructCollectionType(List.class, Integer.class));
        }
        return Collections.emptyList();
    }

    private Boolean checkAvailable() throws IOException {
        Response response = httpClient.getHttpResponse("http://172.22.206.1:8080/api/plants/available");
        InputStream inputStream = getBodyResponse(response);
        if (inputStream != null) {
            return mapper.readValue(inputStream, Boolean.class);
        }
        return false;
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
