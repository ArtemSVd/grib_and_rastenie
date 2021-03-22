package com.example.gribyandrasteniyamap.view.model;

import android.annotation.SuppressLint;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.gribyandrasteniyamap.databse.AppDatabase;
import com.example.gribyandrasteniyamap.databse.entity.Plant;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
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
        LiveData<List<Plant>> liveData = appDatabase.plantDao().getAll();

        return Transformations.switchMap(liveData, (plants) -> {
            List<Plant> deletedPlants = plants.stream()
                    .filter(p -> !(new File(p.getFilePath()).exists()))
                    .peek(this::delete)
                    .collect(Collectors.toList());

            plants.removeAll(deletedPlants);

            return new MutableLiveData<>(plants);
        });
    }


    @SuppressLint("CheckResult")
    public void delete(Plant plant) {
        Observable.fromCallable(() -> appDatabase.plantDao().delete(plant))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

}
