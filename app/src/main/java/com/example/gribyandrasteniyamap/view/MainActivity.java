package com.example.gribyandrasteniyamap.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.gribyandrasteniyamap.R;
import com.example.gribyandrasteniyamap.enums.IntentRequestCode;
import com.example.gribyandrasteniyamap.service.CameraService;
import com.example.gribyandrasteniyamap.service.LocationService;
import com.example.gribyandrasteniyamap.service.PlantService;
import com.example.gribyandrasteniyamap.service.ServerScheduler;
import com.example.gribyandrasteniyamap.service.SharedPreferencesService;
import com.example.gribyandrasteniyamap.utils.Util;
import com.example.gribyandrasteniyamap.view.model.User;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import toothpick.Toothpick;

import static com.example.gribyandrasteniyamap.service.SharedPreferencesService.ENABLE_SCHEDULER;
import static com.example.gribyandrasteniyamap.service.SharedPreferencesService.USERNAME;


public class MainActivity extends AppCompatActivity {

    @Inject
    CameraService cameraService;

    @Inject
    LocationService locationService;

    @Inject
    User user;

    @Inject
    ServerScheduler scheduler;

    @Inject
    SharedPreferencesService sharedPreferencesService;

    @Inject
    PlantService plantService;

    private final String TAG = "MainActivity";
    private int orientation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toothpick.inject(this, Toothpick.openScope("APP"));
        Util.setFullScreen(this);
        setContentView(R.layout.activity_main);
        orientation = Configuration.ORIENTATION_PORTRAIT;

        if (user.getName() == null) {
            FragmentManager manager = getSupportFragmentManager();
            NameDialogFragment nameDialogFragment = new NameDialogFragment();
            nameDialogFragment.show(manager, "nameDialog");
        }

        if (sharedPreferencesService.getBooleanValueByKey(ENABLE_SCHEDULER)) {
            scheduler.run();
        }
    }

    public void onCameraButtonClick(View view) {
        Log.d(TAG, "onCameraButtonClick");
        locationService.turnGpsOn(getApplicationContext(), this);
        cameraService.open(getApplicationContext(), this);
    }

    public void openGallery(View view) {
        Intent intent = new Intent(this, GalleryActivity.class);
        startActivityForResult(intent, IntentRequestCode.REQUEST_GALLERY.getCode());
    }

    public void openMap(View view) {
        Intent intent = new Intent(this, MapActivity.class);
        startActivityForResult(intent, IntentRequestCode.REQUEST_MAP.getCode());
    }

    public void onSettingsClick(View view) {
        findViewById(R.id.settings_view).setVisibility(View.VISIBLE);
        EditText name = findViewById(R.id.settings_name);
        name.setText(sharedPreferencesService.getStringValueByKey(USERNAME));

        Switch schedulerSwitch = findViewById(R.id.settings_switch);
        schedulerSwitch.setOnClickListener(this::onSwitchChanged);
        schedulerSwitch.setChecked(sharedPreferencesService.getBooleanValueByKey(ENABLE_SCHEDULER));
    }

    private void onSwitchChanged(View view) {
        Switch schedulerSwitch = view.findViewById(R.id.settings_switch);

        boolean isEnabled = schedulerSwitch.isChecked();
        sharedPreferencesService.setValue(ENABLE_SCHEDULER, isEnabled);

        if (isEnabled) {
            scheduler.run();
        } else {
            scheduler.stop();
        }
    }

    public void onCloseSettingsClick(View view) {
        findViewById(R.id.settings_view).setVisibility(View.GONE);
    }

    public void onSaveNameSetting(View view) {
        EditText name = findViewById(R.id.settings_name);
        sharedPreferencesService.setValue(USERNAME, name.getText().toString());
        Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
    }

    public void onSyncClick(View view) {
        findViewById(R.id.sync_progressBar_layout).setVisibility(View.VISIBLE);
        plantService.forceLoadOnServerNewThread(this::successSyncCallback, this::errorSyncCallback);
    }

    private void successSyncCallback(Integer syncCount) {
        findViewById(R.id.sync_progressBar_layout).setVisibility(View.GONE);
        Toast.makeText(this, String.format(getString(R.string.sync_elements), syncCount), Toast.LENGTH_SHORT).show();
    }

    private void errorSyncCallback(String message) {
        findViewById(R.id.sync_progressBar_layout).setVisibility(View.GONE);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("CheckResult")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IntentRequestCode.REQUEST_IMAGE_CAPTURE.getCode()) {
            if (resultCode == CameraService.PHOTO_ADDED) {
                changeElementsVisibility(true);
                Observable.fromCallable(() -> cameraService.callback())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::handleCameraSuccessResult, this::handleCameraError);
            }
        } else if (requestCode == IntentRequestCode.REQUEST_PHOTO_DESCRIPTION.getCode()) {
            changeElementsVisibility(false);
            if (resultCode == IntentRequestCode.REQUEST_SAVE_PLANT.getCode()) {
                Toast.makeText(this, getString(R.string.saved), Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == IntentRequestCode.REQUEST_CHECK_GPS.getCode()) {

            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, getString(R.string.gpsOn), Toast.LENGTH_SHORT).show();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, getString(R.string.gpsRequired), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void handleCameraSuccessResult(long id) {
        Log.d(TAG, "onActivityResult: в бд создан новый объект с ид " + id);
        Intent intent = new Intent(this, PhotoDescriptionActivity.class);
        intent.putExtra("id", id);
        startActivityForResult(intent, IntentRequestCode.REQUEST_PHOTO_DESCRIPTION.getCode());
    }

    private void handleCameraError(Throwable throwable) {
        Log.d(TAG, "onActivityResult: ошибка");
        Toast.makeText(this, R.string.camera_error, Toast.LENGTH_SHORT).show();
        changeElementsVisibility(false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        int t = 0;
        if (requestCode == IntentRequestCode.REQUEST_IMAGE_CAPTURE.getCode()) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission granted
                cameraService.open(getApplicationContext(), this);
            }  // permission denied

        } else if (requestCode == IntentRequestCode.REQUEST_GET_GPS.getCode()) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "GPS permission granted");
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        //запрет на поворот экрана
        super.onConfigurationChanged(newConfig);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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

    private void changeElementsVisibility(boolean enableProgressBar) {
        findViewById(R.id.main_topPanel).setVisibility(enableProgressBar ? View.GONE : View.VISIBLE);
        findViewById(R.id.main_bottomPanel).setVisibility(enableProgressBar ? View.GONE : View.VISIBLE);

        findViewById(R.id.main_progressBar).setVisibility(enableProgressBar ? View.VISIBLE : View.GONE);
    }

}