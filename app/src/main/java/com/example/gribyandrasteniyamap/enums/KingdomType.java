package com.example.gribyandrasteniyamap.enums;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum KingdomType {
    NO(0, "Отсутствует"),
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

}
