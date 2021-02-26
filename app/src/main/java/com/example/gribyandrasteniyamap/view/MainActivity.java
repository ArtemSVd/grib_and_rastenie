package com.example.gribyandrasteniyamap.view;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.gribyandrasteniyamap.R;
import com.example.gribyandrasteniyamap.enums.IntentRequestCode;
import com.example.gribyandrasteniyamap.service.CameraService;
import com.example.gribyandrasteniyamap.utils.Util;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import toothpick.Toothpick;


public class MainActivity extends AppCompatActivity {

    @Inject
    CameraService cameraService;

    private final String TAG = "MainActivity";
    private int orientation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toothpick.inject(this, Toothpick.openScope("APP"));
        Util.setFullScreen(this);
        setContentView(R.layout.activity_main);
        orientation = Configuration.ORIENTATION_PORTRAIT;
    }

    public void onCameraButtonClick(View view) {
        Log.d(TAG, "onCameraButtonClick");
        cameraService.open(getApplicationContext(), this);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("CheckResult")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IntentRequestCode.REQUEST_IMAGE_CAPTURE.getCode()) {
            if (resultCode == CameraService.PHOTO_ADDED) {
                // todo: включить какую-нибудь крутилку
                Observable.fromCallable(() -> cameraService.callback())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::handleCameraSuccessResult, this::handleCameraError);
            }
        } else if (requestCode == IntentRequestCode.REQUEST_PHOTO_DESCRIPTION.getCode()) {
            if (resultCode == IntentRequestCode.REQUEST_SAVE_PLANT.getCode()) {
                Toast.makeText(this, "Сохранено", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void handleCameraSuccessResult(long id) {
        Log.d(TAG, "onActivityResult: в бд создан новый объект с ид " + id);
        Intent intent = new Intent(this, PhotoDescriptionActivity.class);
        intent.putExtra("id", id);
        startActivityForResult(intent, IntentRequestCode.REQUEST_PHOTO_DESCRIPTION.getCode());
        // todo: отключить какую-нибудь крутилку
    }

    private void handleCameraError(Throwable throwable) {
        Log.d(TAG, "onActivityResult: ошибка");
        //todo: обработка ошибки
        // todo: отключить какую-нибудь крутилку
    }

    //todo: доработать метод для получение других прав, например, геолокация
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == IntentRequestCode.REQUEST_IMAGE_CAPTURE.getCode()) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission granted
                cameraService.open(getApplicationContext(), this);
            }  // permission denied

        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        orientation = newConfig.orientation;
        setBackgroundImage(newConfig.orientation);
    }

    private void setBackgroundImage(final int orientation) {
        LinearLayout layout;
        layout = (LinearLayout) findViewById(R.id.mainlayout);

        if (orientation == Configuration.ORIENTATION_LANDSCAPE)
            layout.setBackgroundResource(R.drawable.mashrooms_horis);
        else if (orientation == Configuration.ORIENTATION_PORTRAIT)
            layout.setBackgroundResource(R.drawable.mashrooms_vert);
    }

    @Override
    public void onResume() {
        super.onResume();
        setBackgroundImage(orientation);
    }

}