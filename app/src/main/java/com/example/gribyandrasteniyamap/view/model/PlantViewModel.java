package com.example.gribyandrasteniyamap.view.model;

import android.annotation.SuppressLint;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.gribyandrasteniyamap.databse.AppDatabase;
import com.example.gribyandrasteniyamap.databse.entity.Plant;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import toothpick.Toothpick;

public class PlantViewModel extends AndroidViewModel {

    @Inject
    AppDatabase appDatabase;

    @Inject
    public PlantViewModel(@NonNull Application application) {
        super(application);
        Toothpick.inject(this, Toothpick.openScope("APP"));
    }

    public LiveData<List<Plant>> getAll() {
        return appDatabase.plantDao().getAll();
    }


    @SuppressLint("CheckResult")
    public void delete(Plant plant) {
        Observable.fromCallable(() -> appDatabase.plantDao().delete(plant));
    }

}
