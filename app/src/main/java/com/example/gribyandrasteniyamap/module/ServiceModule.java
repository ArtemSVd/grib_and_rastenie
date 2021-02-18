package com.example.gribyandrasteniyamap.module;

import com.example.gribyandrasteniyamap.service.CameraService;

import toothpick.config.Module;

public class ServiceModule extends Module {

    public ServiceModule() {
        bind(CameraService.class);
    }

}
