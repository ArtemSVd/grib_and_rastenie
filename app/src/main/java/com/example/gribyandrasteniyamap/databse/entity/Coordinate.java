package com.example.gribyandrasteniyamap.databse.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Coordinate {
    public String longitude;

    public String latitude;
}
