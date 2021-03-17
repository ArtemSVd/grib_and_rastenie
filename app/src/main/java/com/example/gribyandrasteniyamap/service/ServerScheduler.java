package com.example.gribyandrasteniyamap.service;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ServerScheduler {

    @Inject
    PlantService plantService;

    private final int LOAD_PERIOD = 60;
    private final String TAG = "ServerScheduler";

    @Inject
    public ServerScheduler() {}

    /**
     * Периодическая отправка данных на сервер
     */
    // todo: сделать возможность отключения/включения задачи в зависимости от настроек
    public Disposable run() {
        return Observable.interval(LOAD_PERIOD, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .doOnNext(x -> plantService.loadOnServer())
                .doOnComplete(() -> Log.d(TAG, "Отправка остановлена"))
                .subscribe();
    }

}
