package com.example.gribyandrasteniyamap.view;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.example.gribyandrasteniyamap.R;
import com.example.gribyandrasteniyamap.databse.entity.Plant;
import com.example.gribyandrasteniyamap.enums.KingdomType;
import com.example.gribyandrasteniyamap.service.CameraService;
import com.example.gribyandrasteniyamap.service.PlantService;
import com.example.gribyandrasteniyamap.utils.Util;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import toothpick.Toothpick;

public class PhotoDescriptionActivity extends AppCompatActivity {

    @Inject
    PlantService plantService;

    @Inject
    CameraService cameraService;

    private Plant plant;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toothpick.inject(this, Toothpick.openScope("APP"));
        Util.setFullScreen(this);
        setContentView(R.layout.activity_photo_description);
        changeElementsVisibility();

        Intent intent = getIntent();
        long id = intent.getLongExtra("id", -1L);
        getPlant(id);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("CheckResult")
    private void getPlant(long id) {
        Observable.fromCallable(() -> plantService.getById(id))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleSuccessResult, this::handleError);
    }

    private void handleSuccessResult(Plant plant) {
        this.plant = plant;
        Bitmap image = cameraService.getImage(plant.filePath, this);
        ImageView imageView = findViewById(R.id.photo);
        imageView.setImageBitmap(image);
        Log.d("notag", "onActivityResult: йоу");
        changeElementsVisibility();
    }

    private void handleError(Throwable throwable) {
        Log.d("notag", "onActivityResult: что-то не пошло");
        //todo: вернуться на главную активность, показать ошибку пользователю
    }

    public void onClick(View view) {
        changeElementsVisibility();
    }

    private void changeElementsVisibility() {
        int visibility = findViewById(R.id.photo).getVisibility();
        findViewById(R.id.photo).setVisibility(visibility == View.GONE ? View.VISIBLE : View.GONE);
        findViewById(R.id.name).setVisibility(visibility == View.GONE ? View.VISIBLE : View.GONE);
        findViewById(R.id.description).setVisibility(visibility == View.GONE ? View.VISIBLE : View.GONE);
        findViewById(R.id.kingdomType).setVisibility(visibility == View.GONE ? View.VISIBLE : View.GONE);

        findViewById(R.id.progress_bar).setVisibility(visibility);
    }
}