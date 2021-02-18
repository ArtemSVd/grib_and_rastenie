package com.example.gribyandrasteniyamap.utils;

import android.app.Activity;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class Util {
    public static void setFullScreen(Activity activity) {
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Objects.requireNonNull(((AppCompatActivity)activity).getSupportActionBar()).hide();
    }
}
