package com.example.gribyandrasteniyamap.module;

import com.example.gribyandrasteniyamap.service.CameraService;
import com.example.gribyandrasteniyamap.service.ServerScheduler;
import com.example.gribyandrasteniyamap.service.PlantService;
import com.example.gribyandrasteniyamap.service.ServerSchedulerService;
import com.example.gribyandrasteniyamap.service.http.HttpClient;
import com.example.gribyandrasteniyamap.service.rx.RxCommentService;
import com.example.gribyandrasteniyamap.service.rx.RxPlantService;
import com.example.gribyandrasteniyamap.service.rx.RxSchedulePlantService;

import toothpick.config.Module;

public class ServiceModule extends Module {

    public ServiceModule() {
        bind(PlantService.class);
        bind(CameraService.class);
        bind(HttpClient.class).singleton();
        bind(ServerScheduler.class).singleton();
        bind(ServerSchedulerService.class);
        bind(RxSchedulePlantService.class);
        bind(RxPlantService.class);
        bind(RxCommentService.class);
    }

}
