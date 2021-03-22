package com.example.gribyandrasteniyamap.service;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

@Singleton
public class ServerScheduler {

    @Inject
    ServerSchedulerService serverSchedulerService;

    private final int LOAD_PERIOD = 5;
    private final String TAG = "ServerScheduler";

    private Disposable disposable;

    @Inject
    public ServerScheduler() {
    }

    /**
     * Периодическая отправка данных на сервер
     */
    public void run() {
        if (!isEnabled()) {
            disposable = Observable.interval(LOAD_PERIOD, TimeUnit.SECONDS)
                    .subscribeOn(Schedulers.io())
                    .doOnNext(x -> serverSchedulerService.loadOnServer())
                    .doOnComplete(() -> Log.d(TAG, "Отправка остановлена"))
                    .subscribe();
        } else {
            throw new RuntimeException();
        }
    }

    /**
     * Отключение автоматической отправки данных
     */
    public void stop() {
        if (isEnabled()) {
            disposable.dispose();
        } else {
            throw new RuntimeException();
        }
    }

    public boolean isEnabled() {
        return disposable != null && !disposable.isDisposed();
    }


}
