package com.example.gribyandrasteniyamap.enums;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum KingdomType {
    NO(0, "Не выбрано"),
    MUSHROOMS(1, "Гриб"),
    PLANT(2, "Растение");

    private int id;
    private String name;


    @RequiresApi(api = Build.VERSION_CODES.N)
    public static List<String> nameValues() {
        return Arrays.stream(KingdomType.values())
                .map(v -> v.name)
                .collect(Collectors.toList());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static KingdomType findByName(String name) {
        return Arrays.stream(KingdomType.values())
                .filter(v -> v.name.equals(name))
                .findFirst().orElse(KingdomType.NO);
    }

}
