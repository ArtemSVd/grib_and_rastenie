package com.example.gribyandrasteniyamap.enums;


public enum  IntentRequestCode {
    REQUEST_IMAGE_CAPTURE(1),
    REQUEST_PHOTO_DESCRIPTION(2),
    REQUEST_GALLERY(3),
    REQUEST_SAVE_PLANT(4);

    int code;

    IntentRequestCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
