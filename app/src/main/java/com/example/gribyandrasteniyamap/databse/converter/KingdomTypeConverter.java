package com.example.gribyandrasteniyamap.databse.converter;

import androidx.room.TypeConverter;

import com.example.gribyandrasteniyamap.enums.KingdomType;

public class KingdomTypeConverter {

    @TypeConverter
    public static String fromTypeToString(KingdomType value) {
        return value.name();
    }

    @TypeConverter
    public static KingdomType fromStringToType(String value) {
        return (KingdomType.valueOf(KingdomType.class, value));
    }
}
