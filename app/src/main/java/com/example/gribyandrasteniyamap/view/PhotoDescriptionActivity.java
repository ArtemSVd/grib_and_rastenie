package com.example.gribyandrasteniyamap.view;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.gribyandrasteniyamap.R;
import com.example.gribyandrasteniyamap.databse.entity.Coordinate;
import com.example.gribyandrasteniyamap.databse.entity.Plant;
import com.example.gribyandrasteniyamap.enums.IntentRequestCode;
import com.example.gribyandrasteniyamap.enums.KingdomType;
import com.example.gribyandrasteniyamap.service.CameraService;
import com.example.gribyandrasteniyamap.service.LocationService;
import com.example.gribyandrasteniyamap.service.rx.RxPlantService;
import com.example.gribyandrasteniyamap.utils.Util;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import javax.inject.Inject;

import toothpick.Toothpick;

public class PhotoDescriptionActivity extends AppCompatActivity {

    @Inject
    CameraService cameraService;

    @Inject
    LocationService locationService;

    @Inject
    RxPlantService rxPlantService;

    private final String TAG = "PhotoDescriptionAct";

    private Plant plant;

    boolean editMode;

    boolean plantSaved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toothpick.inject(this, Toothpick.openScope("APP"));
        Util.setFullScreen(this);
        setContentView(R.layout.activity_photo_description);

        Intent intent = getIntent();
        long id = intent.getLongExtra("id", -1L);
        editMode = intent.getBooleanExtra("editMode", false);

        configure();

        changeElementsVisibility(true);

        getPlant(id);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!editMode) {
            deletePlant();
        }
    }


    private void configure() {
        configureSpinner();
        configureNameEditText();
        if (!editMode) {
            configureLocationService();
        }
    }

    private void configureSpinner() {
        Spinner spinner = findViewById(R.id.kingdomType);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, KingdomType.nameValues(getApplicationContext()));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void configureNameEditText() {
        InputFilter filter = (src, start, end, d, dstart, dend) -> {
            for (int i = start; i < end; i++) {
                if (!Character.isLetter(src.charAt(i)) && !Character.isSpaceChar(src.charAt(i))) {
                    return src.subSequence(start, i);
                }
            }
            return null;
        };

        EditText nameView = findViewById(R.id.name);
        nameView.setFilters(new InputFilter[]{filter});
    }

    private void configureLocationService() {
        locationService.fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationService.locationRequest = LocationRequest.create();
        locationService.locationRequest.setInterval(4000);
        locationService.locationRequest.setFastestInterval(2000);
        locationService.locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationService.getLocationPermission(getApplicationContext(), this);
        locationService.checkSettingsAndStartLocationUpdates(this);
    }

    private void getPlant(long id) {
        rxPlantService.getById(id, this::handleSuccessResult, this::handleError);
    }

    private void handleSuccessResult(Plant plant) {
        this.plant = plant;
        ImageView imageView = findViewById(R.id.photo);

        Glide.with(this)
                .load(plant.filePath)
                .into(imageView);

        if (editMode) {
            fillFields(plant);
        }

        changeElementsVisibility(false);
    }

    private void fillFields(Plant plant) {
        EditText name = findViewById(R.id.name);
        name.setText(plant.getName());

        EditText description = findViewById(R.id.description);
        description.setText(plant.getDescription());

        Spinner spinner = findViewById(R.id.kingdomType);

        String typeString = getString(plant.getType().getStringId());
        int i = KingdomType.nameValues(getApplicationContext()).indexOf(typeString);
        spinner.setSelection(Math.max(i, 0));

        if (plant.getCoordinate() != null) {
            TextView tvLong = findViewById(R.id.longitude);
            tvLong.setText(String.valueOf(plant.getCoordinate().getLongitude()));
            TextView tvLat = findViewById(R.id.latitude);
            tvLat.setText(String.valueOf(plant.getCoordinate().getLatitude()));
        }
    }

    private void handleError(Throwable throwable) {
        Log.d(TAG, "onActivityResult: " + throwable.getMessage());
        Toast.makeText(this, getString(R.string.phDescrError), Toast.LENGTH_SHORT).show();
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onSave(View view) {
        if (plant != null) {

            Spinner spinner = findViewById(R.id.kingdomType);
            EditText nameView = findViewById(R.id.name);
            EditText descriptionView = findViewById(R.id.description);
            TextView longitudeView = findViewById(R.id.longitude);
            TextView latitudeView = findViewById(R.id.latitude);

            if (nameView.getText() == null || nameView.getText().toString().replaceAll(" ", "").isEmpty()) {
                Toast.makeText(this, getString(R.string.enterName), Toast.LENGTH_SHORT).show();
                return;
            }

            plant.setType(KingdomType.findByName((String) spinner.getSelectedItem(), getApplicationContext()));
            plant.setName(nameView.getText().toString());
            plant.setDescription(descriptionView.getText().toString());

            plant.setCoordinate(Coordinate.builder()
                    .latitude(latitudeView.getText().toString())
                    .longitude(longitudeView.getText().toString())
                    .build()
            );

            plant.setSynchronized(false);

            rxPlantService.update(plant, () -> {
                setResult(IntentRequestCode.REQUEST_SAVE_PLANT.getCode());
                plantSaved = true;
                finish();
            });
        }

    }

    public void onCancel(View view) {
        if (editMode) {
            finish();
        } else {
            deletePlant();
        }
    }

    private void deletePlant() {
        if (!plantSaved) {
            rxPlantService.delete(plant, this::finish);
        }
    }

    private void changeElementsVisibility(boolean enableProgressBar) {
        findViewById(R.id.LinerLayout2).setVisibility(enableProgressBar ? View.GONE : View.VISIBLE);
        findViewById(R.id.longitudeText).setVisibility(enableProgressBar ? View.GONE : View.VISIBLE);
        findViewById(R.id.longitude).setVisibility(enableProgressBar ? View.GONE : View.VISIBLE);
        findViewById(R.id.latitudeText).setVisibility(enableProgressBar ? View.GONE : View.VISIBLE);
        findViewById(R.id.latitude).setVisibility(enableProgressBar ? View.GONE : View.VISIBLE);
        findViewById(R.id.cancelButton).setVisibility(enableProgressBar ? View.GONE : View.VISIBLE);
        findViewById(R.id.saveButton).setVisibility(enableProgressBar ? View.GONE : View.VISIBLE);

        findViewById(R.id.progress_bar).setVisibility(enableProgressBar ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationService.locationPermissionGranted = false;
        if (requestCode == IntentRequestCode.REQUEST_GET_GPS.getCode()) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationService.locationPermissionGranted = true;
                locationService.checkSettingsAndStartLocationUpdates(this);
            }
        }
    }

}