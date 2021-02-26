package com.example.gribyandrasteniyamap.enums;


public enum  IntentRequestCode {
    REQUEST_IMAGE_CAPTURE(1),
    REQUEST_PHOTO_DESCRIPTION(2),
    REQUEST_SAVE_PLANT(3);

    int code;

    IntentRequestCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}