package com.example.gribyandrasteniyamap.view;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gribyandrasteniyamap.R;
import com.example.gribyandrasteniyamap.databse.entity.Coordinate;
import com.example.gribyandrasteniyamap.dto.PlantDto;
import com.example.gribyandrasteniyamap.dto.PlantsRequestParams;
import com.example.gribyandrasteniyamap.enums.IntentRequestCode;
import com.example.gribyandrasteniyamap.enums.KingdomType;
import com.example.gribyandrasteniyamap.service.PlantService;
import com.example.gribyandrasteniyamap.view.model.MarkerTag;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import toothpick.Toothpick;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    @Inject
    PlantService plantService;

    private final List<CheckBox> checkBoxes = new ArrayList<>();
    private ProgressBar progressBar;
    private static int COUNT = 0;
    private static int numOfRequest = 2;
    private CheckBox isLocalCheckBox;

    private GoogleMap mMap;
    public boolean locationPermissionGranted;
    FusedLocationProviderClient fusedLocationProviderClient;
    Location lastKnownLocation;
    LatLng defaultLocation = new LatLng(-34, 151);
    private static final int DEFAULT_ZOOM = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Toothpick.inject(this, Toothpick.openScope("APP"));

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        progressBar = findViewById(R.id.progressBar);

        LinearLayout linearLayout = findViewById(R.id.checkBoxGroup);
        Arrays.stream(KingdomType.values())
                .filter(t -> !t.equals(KingdomType.NO))
                .forEach(type -> {
                    CheckBox checkBox = new CheckBox(this);
                    checkBox.setText(getString(type.getStringId()));
                    checkBox.setId(type.getId());
                    linearLayout.addView(checkBox);
                    checkBoxes.add(checkBox);
                });
    }

    public void openSearch(View view) {
        findViewById(R.id.search).setVisibility(View.GONE);
        findViewById(R.id.searchView).setVisibility(View.VISIBLE);
    }

    public void cancelSearch(View view) {
        findViewById(R.id.search).setVisibility(View.VISIBLE);
        findViewById(R.id.searchView).setVisibility(View.GONE);
    }

    public void onFindClick(View view) {
        find();
    }

    private void find() {
        progressBar.setVisibility(View.VISIBLE);

        cancelSearch(null);
        mMap.clear();

        isLocalCheckBox = findViewById(R.id.checkBoxOnlyLocal);

        List<KingdomType> checkedTypes = checkBoxes.stream()
                .filter(CompoundButton::isChecked)
                .map(View::getId)
                .map(KingdomType::findById)
                .collect(Collectors.toList());

        String name = ((EditText) findViewById(R.id.maps_find_name)).getText().toString().trim();

        PlantsRequestParams params = PlantsRequestParams.builder()
                .name(name)
                .kingdomTypes(checkedTypes.isEmpty() ? Arrays.asList(KingdomType.values()) : checkedTypes).build();

        plantService.getPlants(params, this::renderMarkers, isLocalCheckBox.isChecked());
        numOfRequest = isLocalCheckBox.isChecked() ? 1 : 2;
    }

    private void renderMarkers(List<PlantDto> plants) {
        // todo: возможно с большим количеством меток будет долго работать, лучше делать в отдельном потоке ?
        // todo: возможно стоит использовать кластеризацию маркеров
        for (PlantDto plant : plants) {
            Coordinate coordinate = plant.getCoordinate();
            if (coordinate != null) {
                LatLng latLng = new LatLng(Double.parseDouble(coordinate.latitude), Double.parseDouble(coordinate.longitude));

                String snippetText = plant.getType().toString() + "\n" + plant.getDescription() + "\n"
                                     + getString(R.string.longitude) + plant.getCoordinate().getLongitude() + "\n"
                                     + getString(R.string.latitude) + plant.getCoordinate().getLatitude();
                mMap.addMarker(new MarkerOptions().position(latLng)
                        .title(plant.getName())
                        .snippet(snippetText)
                        .icon(BitmapDescriptorFactory.fromResource(getIcon(plant.getType()))))
                        .setTag(MarkerTag.builder().plantId(plant.getId()).isLocal(plant.isLocal()).build());
            }
        }

        // Костыль, не придумал как сделать по-другому
        COUNT++;
        if (COUNT == numOfRequest) {
            progressBar.setVisibility(View.GONE);
            COUNT = 0;
        }
    }

    private int getIcon(KingdomType kingdomType) {
        switch (kingdomType) {
            case MUSHROOMS:
                return R.drawable.mushroom_icon;
            case PLANT:
                return R.drawable.plant_icon;
            default:
                return R.drawable.default_icon;
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        updateLocationUI();
        CustomInfoWindowAdapter customInfoWindowAdapter = new CustomInfoWindowAdapter(this);
        mMap.setInfoWindowAdapter(customInfoWindowAdapter);

        getDeviceLocation();
        mMap.setOnInfoWindowClickListener(customInfoWindowAdapter);

        find();
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
           // Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    IntentRequestCode.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION.getCode());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        if (requestCode == IntentRequestCode.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION.getCode()) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
            }
        }
        updateLocationUI();
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }
                        } else {
                            //Log.d(TAG, "Current location is null. Using defaults.");
                            //Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            //Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter, GoogleMap.OnInfoWindowClickListener {
        private final View mWindow;
        private final View mContents;
        private final Context context;

        CustomInfoWindowAdapter(Context context) {
            this.context = context;
            mWindow = getLayoutInflater().inflate(R.layout.map_item_info, null);
            mContents = getLayoutInflater().inflate(R.layout.map_item_info, null);

        }

        private void render(Marker marker, View view) {
            TextView tvTitle = view.findViewById(R.id.title);
            TextView tvSnippet = view.findViewById(R.id.snippet);

            tvTitle.setText(marker.getTitle());
            tvSnippet.setText(marker.getSnippet());
        }

        @Override
        public View getInfoWindow(Marker marker) {
            render(marker, mWindow);
            return mWindow;
        }

        @Override
        public View getInfoContents(Marker marker) {
            render(marker, mWindow);
            return mWindow;
        }

        @Override
        public void onInfoWindowClick(Marker marker) {
            MarkerTag markerTag = (MarkerTag) marker.getTag();
            Toast.makeText(context, markerTag.toString(), Toast.LENGTH_LONG).show();
        }

    }
}