package com.example.gribyandrasteniyamap.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum KingdomType {
    NO(0, "Не выбрано"),
    MUSHROOMS(1, "Гриб"),
    PLANT(2, "Растение");

    private final int id;
    private final String name;


    public static List<String> nameValues() {
        return Arrays.stream(KingdomType.values())
                .map(v -> v.name)
                .collect(Collectors.toList());
    }

    public static KingdomType findByName(String name) {
        return Arrays.stream(KingdomType.values())
                .filter(v -> v.name.equals(name))
                .findFirst().orElse(KingdomType.NO);
    }

}
