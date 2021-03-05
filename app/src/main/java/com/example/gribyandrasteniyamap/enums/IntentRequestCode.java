package com.example.gribyandrasteniyamap.enums;


public enum  IntentRequestCode {
    REQUEST_IMAGE_CAPTURE(1),
    REQUEST_PHOTO_DESCRIPTION(2),
    REQUEST_SAVE_PLANT(3),
    REQUEST_CHECK_GPS(4),
    REQUEST_GET_GPS(5),
    REQUEST_GALLERY(6);

    int code;

    IntentRequestCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
