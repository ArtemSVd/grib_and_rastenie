package com.example.gribyandrasteniyamap.databse.dao;

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
    List<Plant> getAll();

    @Query("SELECT * from Plant where id = :id")
    Plant getById(long id);

    @Insert
    long insert(Plant plant);

    @Update
    int update(Plant plant);

    @Delete
    int delete(Plant plant);

}
