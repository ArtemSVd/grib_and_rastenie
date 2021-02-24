package com.example.gribyandrasteniyamap.databse.entity;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.gribyandrasteniyamap.enums.KingdomType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Plant {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public String filePath;

    public String name;

    public String description;

    public KingdomType type;

    @Embedded
    public Coordinate coordinate;

}
