package com.example.gribyandrasteniyamap.service.rx;

import android.annotation.SuppressLint;
import android.util.Log;

import com.example.gribyandrasteniyamap.service.ServerSchedulerService;

import java.util.function.Consumer;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RxSchedulePlantService {
    @Inject
    ServerSchedulerService serverSchedulerService;

    @Inject
    RxSchedulePlantService() {
    }

    @SuppressLint("CheckResult")
    public void forceLoadOnServerNewThread(Consumer<Integer> successCallback, Consumer<String> errorCallback) {
        Observable.fromCallable(() -> {
            serverSchedulerService.forceLoadOnServer(successCallback, errorCallback);
            return 0;
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        e -> Log.e("RxSchedulePlantService", "Успешная загрузка данных на сервер"),
                        e -> errorCallback.accept(e.getMessage()));
    }

}
