package com.example.gribyandrasteniyamap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gribyandrasteniyamap.dto.Plant;
import com.example.gribyandrasteniyamap.dto.PlantsRequestParams;
import com.example.gribyandrasteniyamap.service.http.PlantHttpClient;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private int orientation;
    PlantHttpClient plantHttpService = new PlantHttpClient();
    String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreen();
        setContentView(R.layout.activity_main);
        orientation = Configuration.ORIENTATION_PORTRAIT;
        findViewById(R.id.imageView2).setVisibility(View.GONE);
        findViewById(R.id.textView2).setVisibility(View.GONE);
        findViewById(R.id.textView3).setVisibility(View.GONE);
        findViewById(R.id.progress_bar).setVisibility(View.GONE);
    }

    private void setFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Objects.requireNonNull(getSupportActionBar()).hide();
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

    public void onClick3(View view) throws IOException {
        findViewById(R.id.imageView2).setVisibility(View.GONE);
        findViewById(R.id.textView2).setVisibility(View.GONE);
        findViewById(R.id.textView3).setVisibility(View.GONE);
        findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
        Observable<List<Plant>> values = Observable.fromCallable(() -> plantHttpService.getPlants(PlantsRequestParams.builder().kingdomType(KingdomType.PLANT).build()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        Disposable disposable =  values.subscribe(
                plants -> {
                    for (Plant plant : plants) {
                        filePath = plant.getFilePath();
                        TextView textView1 = findViewById(R.id.textView2);
                        textView1.setText(plant.getName());
                        TextView textView2 = findViewById(R.id.textView3);
                        textView2.setText(plant.getDescription());
                        onClick(null);
                    }

                },
                err -> {
                    Toast.makeText(getApplicationContext(), "Ошибка при получении файла", Toast.LENGTH_SHORT)
                            .show();
                }
        );
    }

    public void onClick2(View view) throws IOException {
        findViewById(R.id.imageView2).setVisibility(View.GONE);
        findViewById(R.id.textView2).setVisibility(View.GONE);
        findViewById(R.id.textView3).setVisibility(View.GONE);
        findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);

        Observable<List<Plant>> values = Observable.fromCallable(() -> plantHttpService.getPlants(PlantsRequestParams.builder().kingdomType(KingdomType.MUSHROOMS).build()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        Disposable disposable =  values.subscribe(
                plants -> {
                    for (Plant plant : plants) {
                        filePath = plant.getFilePath();
                        TextView textView1 = findViewById(R.id.textView2);
                        textView1.setText(plant.getName());
                        TextView textView2 = findViewById(R.id.textView3);
                        textView2.setText(plant.getDescription());
                        onClick(null);
                    }

                },
                err -> {
                    Toast.makeText(getApplicationContext(), "Ошибка при получении файла", Toast.LENGTH_SHORT)
                            .show();
                }
        );
    }

    public void onClick(View view) throws IOException {
        Observable<Bitmap> values = Observable.fromCallable(() -> plantHttpService.getImage(filePath))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        ;
        Disposable disposable =  values.subscribe(
                bitmap -> {
                    if (bitmap != null) {
                        ImageView imageView = findViewById(R.id.imageView2);
                        imageView.setImageBitmap(bitmap);
                        findViewById(R.id.imageView2).setVisibility(View.VISIBLE);
                        findViewById(R.id.textView2).setVisibility(View.VISIBLE);
                        findViewById(R.id.textView3).setVisibility(View.VISIBLE);
                        findViewById(R.id.progress_bar).setVisibility(View.GONE);
                    }
                },
                err -> {
                    Toast.makeText(getApplicationContext(), "Ошибка при получении файла", Toast.LENGTH_SHORT)
                            .show();
                }
        );
    }

}