package com.example.gribyandrasteniyamap.service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.example.gribyandrasteniyamap.R;
import com.example.gribyandrasteniyamap.enums.IntentRequestCode;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Consumer;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class CameraService {
    private Context context;
    private Activity activity;

    @Inject
    PlantService plantService;

    private String TAG = "CameraService";

    public static final int PHOTO_ADDED = -1;

    private static String currentPhotoPath;

    @Inject
    public CameraService() {
    }

    public void open(Context ctxt, Activity act) {
        context = ctxt;
        activity = act;

        if (context != null && activity != null) {
            if (isStoragePermissionGranted()) {
                dispatchTakePictureIntent();
            }
        }
    }


    @SuppressLint("CheckResult")
    public void processCameraResult(Consumer<Long> successCallback, Consumer<Throwable> errorCallback) {
        Observable.fromCallable(this::process)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(successCallback::accept, errorCallback::accept);
    }

    /**
     * Метод для обработки успешного результата работы камеры
     * @return идентификатор новой записи в бд
     */
    public long process() {
        galleryAddPic();
        return plantService.insertNewPlant(currentPhotoPath);
    }

    private boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {
                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(context, context.getString(R.string.camera_error), Toast.LENGTH_SHORT).show();
            }

            if (photoFile != null) {
                try {
                    Uri photoURI = FileProvider.getUriForFile(context,
                            "com.example.android.fileprovider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    activity.startActivityForResult(takePictureIntent, IntentRequestCode.REQUEST_IMAGE_CAPTURE.getCode());
                } catch (Exception esx) {
                    Toast.makeText(context, context.getString(R.string.camera_error), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, context.getString(R.string.camera_error), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, context.getString(R.string.camera_error), Toast.LENGTH_SHORT).show();
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "GRIB_JPEG_" + timeStamp + "_";
        File storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        //File storageDir = ;
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void galleryAddPic() {
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, contentUri);
        context.sendBroadcast(mediaScanIntent);
    }
}
