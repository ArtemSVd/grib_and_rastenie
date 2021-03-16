package com.example.gribyandrasteniyamap.module;

import com.example.gribyandrasteniyamap.view.model.User;

import toothpick.config.Module;

public class UserModule extends Module {
    public UserModule(User user) {
        bind(User.class).toInstance(user);
    }
}
