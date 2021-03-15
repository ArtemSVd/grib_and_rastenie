package com.example.gribyandrasteniyamap.service;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.example.gribyandrasteniyamap.enums.IntentRequestCode;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.Task;
import java.util.function.Consumer;

import javax.inject.Inject;


public class LocationService {
    private Context context;
    private Activity activity;

    private final String TAG = "LocationService";

    @Inject
    public LocationService() {
    }

    public void turnGpsOn(Context cntxt, Activity actvt) {
        context = cntxt;
        activity = actvt;

        LocationRequest locationRequest;
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(context)
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(task -> {
            try {
                LocationSettingsResponse response = task.getResult(ApiException.class);
                Toast.makeText(activity, "GPS is ON", Toast.LENGTH_SHORT).show();
            } catch (ApiException e) {
                switch (e.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                        try {
                            ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                            resolvableApiException.startResolutionForResult(activity, IntentRequestCode.REQUEST_CHECK_GPS.getCode());
                        } catch (IntentSender.SendIntentException sendIntentException) {
                            // todo
                        }
                        break;

                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }

    public void getCurrentLocation(Context cntxt, Activity actvt, Consumer<Location> function) {
        context = cntxt;
        activity = actvt;
        FusedLocationProviderClient fusedLocationProviderClient;
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                fusedLocationProviderClient.getLastLocation()
                        .addOnSuccessListener(location -> {
                            if (location != null) {
                                function.accept(location);
                            }
                        });
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, IntentRequestCode.REQUEST_GET_GPS.getCode());
            }
        }
    }

}
