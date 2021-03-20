package com.example.gribyandrasteniyamap.view;

import androidx.appcompat.app.AppCompatActivity;

import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.gribyandrasteniyamap.R;
import com.example.gribyandrasteniyamap.databse.entity.Plant;
import com.example.gribyandrasteniyamap.enums.IntentRequestCode;
import com.example.gribyandrasteniyamap.service.PlantService;
import com.example.gribyandrasteniyamap.utils.Util;
import com.example.gribyandrasteniyamap.view.adapter.ImageGalleryAdapter;
import com.example.gribyandrasteniyamap.view.model.PlantViewModel;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import toothpick.Toothpick;

public class GalleryActivity extends AppCompatActivity {

    @Inject
    PlantService plantService;

    @Inject
    ImageGalleryAdapter adapter;

    private PlantViewModel plantViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setFullScreen(this);
        Toothpick.inject(this, Toothpick.openScope("APP"));
        setContentView(R.layout.activity_gallery);
        plantViewModel = ViewModelProviders.of(this).get(PlantViewModel.class);
        getPlants();
    }

    private void openPhotoDescriptionActivity(Long plantId) {
        Intent intent = new Intent(this, PhotoDescriptionActivity.class);
        intent.putExtra("id", plantId);
        intent.putExtra("editMode", true);
        startActivityForResult(intent, IntentRequestCode.REQUEST_PHOTO_DESCRIPTION.getCode());
    }

    @SuppressLint("CheckResult")
    private void getPlants() {
        plantViewModel.getAll().observe(this, plants -> {
            List<Plant> deletedPlants = plants.stream()
                    .filter(p -> !(new File(p.getFilePath()).exists()))
                    .peek(p -> plantViewModel.delete(p))
                    .collect(Collectors.toList());

            plants.removeAll(deletedPlants);

            changeElementsVisible(plants.isEmpty());

            if (!plants.isEmpty()) {
                RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 3);
                RecyclerView recyclerView = findViewById(R.id.rv_images);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(layoutManager);

                adapter.setmContext(this);
                adapter.setPlants(plants);
                adapter.setOnClickFunction(this::openPhotoDescriptionActivity);
                recyclerView.setAdapter(adapter);
            }
        });
    }

    private void changeElementsVisible(boolean isEmpty) {
        LinearLayout galleryEmpty = findViewById(R.id.galleryEmpty);
        LinearLayout galleryNotEmpty = findViewById(R.id.galleryNotEmpty);

        galleryEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        galleryNotEmpty.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }
}