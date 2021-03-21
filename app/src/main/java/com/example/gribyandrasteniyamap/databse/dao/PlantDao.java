package com.example.gribyandrasteniyamap.databse.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.gribyandrasteniyamap.databse.entity.Plant;

import java.util.List;

@Dao
public interface PlantDao {

    @Query("SELECT * from Plant")
    LiveData<List<Plant>> getAll();

    @Query("SELECT * from Plant where id = :id")
    Plant getById(long id);

    @Query("SELECT * from Plant where upper(name) like upper('%' || :name || '%') and type in (:kingdomTypes) and isSynchronized = :isSynchronized")
    List<Plant> getPlants(String name, List<String> kingdomTypes, boolean isSynchronized);

    @Query("SELECT * from Plant where upper(name) like upper('%' || :name || '%') and type in (:kingdomTypes)")
    List<Plant> getPlants(String name, List<String> kingdomTypes);

    @Query("SELECT * from Plant where isSynchronized = :isSynchronized and latitude is not null and longitude is not null limit :limit")
    List<Plant> getPlants(Integer limit, boolean isSynchronized);

    @Query("SELECT id from Plant where isSynchronized = 0 and syncDate is not null")
    List<Integer> getExcludedPlantIds();

    @Query("SELECT count(1) from Plant where isSynchronized = 0")
    Integer getNotSyncCount();

    @Insert
    long insert(Plant plant);

    @Update
    int update(Plant plant);

    @Delete
    int delete(Plant plant);

}
