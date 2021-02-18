package com.example.gribyandrasteniyamap.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.gribyandrasteniyamap.R;
import com.example.gribyandrasteniyamap.utils.Util;

import toothpick.Toothpick;

public class PhotoDescriptionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toothpick.inject(this, Toothpick.openScope("APP"));
        Util.setFullScreen(this);
        setContentView(R.layout.activity_photo_description);
        changeElementsVisibility();

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