package com.example.gribyandrasteniyamap.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.example.gribyandrasteniyamap.R;
import com.example.gribyandrasteniyamap.databse.entity.Plant;
import com.example.gribyandrasteniyamap.service.PlantService;
import com.example.gribyandrasteniyamap.utils.Util;
import com.example.gribyandrasteniyamap.view.adapter.ImageGalleryAdapter;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import toothpick.Toothpick;

public class GalleryActivity extends AppCompatActivity {

    @Inject
    PlantService plantService;

    @Inject
    ImageGalleryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setFullScreen(this);
        Toothpick.inject(this, Toothpick.openScope("APP"));
        setContentView(R.layout.activity_gallery);

        getPlants();
    }

    @SuppressLint("CheckResult")
    private void getPlants() {
        Observable.fromCallable(() -> plantService.getAll())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleSuccessResult, this::handleError);
    }

    private void handleSuccessResult(List<Plant> plants) {
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 3);
        RecyclerView recyclerView = findViewById(R.id.rv_images);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        adapter.setmContext(this);
        adapter.setPlants(plants);
        recyclerView.setAdapter(adapter);

    }

    private void handleError(Throwable throwable) {
        Log.d("notag", "onActivityResult: что-то не пошло");
        //todo: вернуться на главную активность, показать ошибку пользователю
    }
}