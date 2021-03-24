package com.example.gribyandrasteniyamap.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gribyandrasteniyamap.R;
import com.example.gribyandrasteniyamap.dto.CommentDto;
import com.example.gribyandrasteniyamap.dto.PlantDto;
import com.example.gribyandrasteniyamap.enums.KingdomType;
import com.example.gribyandrasteniyamap.service.rx.RxCommentService;
import com.example.gribyandrasteniyamap.service.rx.RxPlantService;
import com.example.gribyandrasteniyamap.utils.Util;
import com.example.gribyandrasteniyamap.view.adapter.CommentsAdapter;
import com.example.gribyandrasteniyamap.view.model.User;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import toothpick.Toothpick;

public class MapViewActivity extends AppCompatActivity {

    @Inject
    RxPlantService rxPlantService;

    @Inject
    CommentsAdapter commentsAdapter;

    @Inject
    RxCommentService rxCommentService;

    @Inject
    User user;

    private PlantDto plant;
    private boolean isLocal;
    private long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setFullScreen(this);
        Toothpick.inject(this, Toothpick.openScope("APP"));
        setContentView(R.layout.activity_map_view);

        Intent intent = getIntent();
        id = intent.getLongExtra("id", -1L);
        isLocal = intent.getBooleanExtra("isLocal", true);

        Spinner spinner = findViewById(R.id.kingdomType);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, KingdomType.nameValues(getApplicationContext()));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        getPlant(id, isLocal);
    }

    public void hideKeyboard(View view){
        InputMethodManager manager
                = (InputMethodManager)
                getSystemService(
                        this.INPUT_METHOD_SERVICE);
        manager
                .hideSoftInputFromWindow(
                        view.getWindowToken(), 0);
    }

    public void scrollDown(View view) {
        ScrollView scrollView = findViewById(R.id.scroll);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void addComment(View view) {
        EditText editText = findViewById(R.id.comment_text_text);

        hideKeyboard(view);

        if (editText.getText().length() == 0) {
            return;
        }

        CommentDto commentDto = CommentDto.builder()
                .text(editText.getText().toString())
                .createdDate(new Date())
                .plantId(id)
                .user(user).build();

        rxCommentService.addComment(commentDto, this::addCommentCallback);
    }

    private void addCommentCallback(CommentDto commentDto) {
        if (commentsAdapter.getComments() == null) {
            initCommentsRecycleView(new ArrayList<CommentDto>() {{
                add(commentDto);
            }});
        } else {
            commentsAdapter.addComment(commentDto);
        }

        EditText editText = findViewById(R.id.comment_text_text);
        editText.getText().clear();
    }

    @SuppressLint("CheckResult")
    private void getPlant(long id, boolean isLocal) {
        changeElementsVisibility(true);
        rxPlantService.getPlant(id, isLocal, this::callbackPlant);
    }

    private void callbackPlant(PlantDto plant) {
        this.plant = plant;

        EditText name = findViewById(R.id.name);
        name.setText(plant.getName());
        name.setFocusable(false);

        EditText description = findViewById(R.id.description);
        description.setText(plant.getDescription());
        description.setFocusable(false);
        description.setMovementMethod(new ScrollingMovementMethod());

        if (plant.getCoordinate() != null) {
            TextView longitude = findViewById(R.id.longitude);
            longitude.setText(plant.getCoordinate().getLongitude());

            TextView latitude = findViewById(R.id.latitude);
            latitude.setText(plant.getCoordinate().getLatitude());
        }

        Spinner spinner = findViewById(R.id.kingdomType);

        String typeString = getString(plant.getType().getStringId());
        int i = KingdomType.nameValues(getApplicationContext()).indexOf(typeString);
        spinner.setSelection(Math.max(i, 0));
        spinner.setEnabled(false);

        rxCommentService.getComments(isLocal ? plant.getServerId() : plant.getId(), this::initCommentsRecycleView);

        ImageView imageView = findViewById(R.id.photo);
        if (plant.isLocal()) {
            Glide.with(this)
                    .load(plant.getFilePath())
                    .into(imageView);
        } else {
            try {
                String filePath = plant.getFilePath().replace("\\", "/");
                URL url = new URL("http://172.22.206.1:8080/api/plants/" + filePath);

                Glide.with(this)
                        .load(url)
                        .into(imageView);

            } catch (MalformedURLException e) {
                //todo: вывести ошибку и вернуться на карту
                e.printStackTrace();
            }

        }
    }

    private void initCommentsRecycleView(List<CommentDto> comments) {
        if (!comments.isEmpty()) {
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 1) ;

            RecyclerView recyclerView = findViewById(R.id.rv_images);
            //recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(layoutManager);
            commentsAdapter.setmContext(this);
            commentsAdapter.setComments(comments);
            recyclerView.setAdapter(commentsAdapter);
        }
        changeElementsVisibility(false);
    }

    private void changeElementsVisibility(boolean enableProgressBar) {
        findViewById(R.id.LinerLayout2).setVisibility(enableProgressBar ? View.GONE : View.VISIBLE);
        findViewById(R.id.longitudeText).setVisibility(enableProgressBar ? View.GONE : View.VISIBLE);
        findViewById(R.id.longitude).setVisibility(enableProgressBar ? View.GONE : View.VISIBLE);
        findViewById(R.id.latitudeText).setVisibility(enableProgressBar ? View.GONE : View.VISIBLE);
        findViewById(R.id.latitude).setVisibility(enableProgressBar ? View.GONE : View.VISIBLE);

        findViewById(R.id.progress_bar).setVisibility(enableProgressBar ? View.VISIBLE : View.GONE);
    }


}
