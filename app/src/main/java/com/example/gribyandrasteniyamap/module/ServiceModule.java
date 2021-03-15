package com.example.gribyandrasteniyamap.module;

import com.example.gribyandrasteniyamap.service.CameraService;
import com.example.gribyandrasteniyamap.service.PlantService;
import com.example.gribyandrasteniyamap.service.http.HttpClient;

import toothpick.config.Module;

public class ServiceModule extends Module {

    public ServiceModule() {
        bind(PlantService.class);
        bind(CameraService.class);
        bind(HttpClient.class).singleton();
    }

}
