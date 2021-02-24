package com.example.gribyandrasteniyamap.databse.converter;

import androidx.room.TypeConverter;

import com.example.gribyandrasteniyamap.enums.KingdomType;

public class KingdomTypeConverter {

    @TypeConverter
    public static int fromTypeToInt(KingdomType value) {
        return value.ordinal();
    }

    @TypeConverter
    public static KingdomType fromIntToType(int value) {
        return (KingdomType.values()[value]);
    }
}
