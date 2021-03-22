package com.example.gribyandrasteniyamap.service.rx;

import android.annotation.SuppressLint;

import androidx.core.util.Consumer;

import com.example.gribyandrasteniyamap.databse.entity.Plant;
import com.example.gribyandrasteniyamap.dto.PlantDto;
import com.example.gribyandrasteniyamap.dto.PlantsRequestParams;
import com.example.gribyandrasteniyamap.service.PlantService;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RxPlantService {
    @Inject
    PlantService plantService;

    @Inject
    RxPlantService() {
    }

    @SuppressLint("CheckResult")
    public void delete(Plant plant, Runnable successCallback) {
        Observable.fromCallable(() -> plantService.delete(plant))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(r -> successCallback.run());
    }

    @SuppressLint("CheckResult")
    public void update(Plant plant, Runnable successCallback) {
        Observable.fromCallable(() -> plantService.update(plant))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(r -> successCallback.run());
    }

    @SuppressLint("CheckResult")
    public void getById(long id, Consumer<Plant> successCallback, Consumer<Throwable> errorCallback) {
        Observable.fromCallable(() -> plantService.getById(id))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(successCallback::accept, errorCallback::accept);
    }

    @SuppressLint("CheckResult")
    public void getPlants(PlantsRequestParams params, Consumer<List<PlantDto>> callback, boolean onlyLocal) {
        Observable.fromCallable(() -> plantService.getPlantsFromDb(params, onlyLocal))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback::accept);

        if (!onlyLocal) {
            Observable.fromCallable(() -> plantService.getPlantsFromServer(params))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(callback::accept);
        }
    }

}
