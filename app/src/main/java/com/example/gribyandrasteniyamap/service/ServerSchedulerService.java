package com.example.gribyandrasteniyamap.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.gribyandrasteniyamap.R;
import com.example.gribyandrasteniyamap.databse.entity.Plant;
import com.example.gribyandrasteniyamap.dto.PlantDto;
import com.example.gribyandrasteniyamap.service.http.PlantsAppClient;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ServerSchedulerService {
    @Inject
    PlantsAppClient httpClient;

    @Inject
    PlantService plantService;

    @Inject
    Context context;

    @Inject
    ServerSchedulerService() {
    }

    private final int LOAD_LIMIT = 5;

    @SuppressLint("CheckResult")
    public void forceLoadOnServer(Consumer<Integer> successCallback, Consumer<String> errorCallback) {
        try {
            Log.i("PlantService", "Принудительная загрузка данных на сервер");
            if (Boolean.TRUE.equals(httpClient.checkAvailable())) {
                Log.i("PlantService", "Сервер доступен");
                Integer notSyncCount = plantService.getNotSyncCount();
                int repeat = notSyncCount != null ? (int) Math.ceil(notSyncCount / (float) LOAD_LIMIT) : 0;

                Log.i("PlantService", String.format("Количество запросов на отправку: %d", repeat));
                if (repeat == 0) {
                    throw new RuntimeException(context.getString(R.string.sync_no_item));
                }

                Observable.timer(2, TimeUnit.SECONDS)
                        .subscribeOn(Schedulers.io())
                        .repeat(repeat)
                        .doOnNext(x -> loadOnServer())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnComplete(() -> successCallback.accept(notSyncCount))
                        .subscribe(
                                e -> Log.i("PlantService", e.toString()),
                                e -> errorCallback.accept(context.getString(R.string.sync_error)));

            } else {
                Log.i("PlantService", "Сервер недоступен");
                throw new RuntimeException(context.getString(R.string.sync_serv_unavailable));
            }
        } catch (IOException e) {
            Log.e("PlantService", e.getMessage());
            throw new RuntimeException(context.getString(R.string.sync_error));
        }
    }

    public void loadOnServer() {
        Log.i("PlantService", "Загрузка данных на сервер");

        List<PlantDto> plants = plantService.getPlantsForSync(LOAD_LIMIT);

        Log.i("PlantService", plants.toString());

        if (plants.isEmpty()) {
            Log.i("PlantService", "Нечего загружать");
            return;
        }

        List<File> files = plants.stream()
                .map(plant -> getFile(plant.getFilePath()))
                .collect(Collectors.toList());

        try {
            Map<Integer, Long> plantsIdToServerIds = httpClient.load(plants, files);
            Log.i("PlantService", "Идентификаторы загруженных сущностей: " + plantsIdToServerIds);

            plantsIdToServerIds.entrySet()
                    .forEach(row -> {
                        Plant plant = plantService.getById(row.getKey());
                        plant.setSynchronized(true);
                        plant.setServerId(row.getValue());
                        plantService.update(plant);
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
}
