package com.example.gribyandrasteniyamap.enums;

import android.content.Context;

import com.example.gribyandrasteniyamap.R;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum KingdomType {
    NO(0, R.string.kingdomType_no),
    MUSHROOMS(1, R.string.kingdomType_mushrooms),
    PLANT(2, R.string.kingdomType_plants);

    private final int id;
    private final int stringId;

    public static List<String> nameValues(Context context) {
        return Arrays.stream(KingdomType.values())
                .map(v -> context.getString(v.stringId))
                .collect(Collectors.toList());
    }

    public static KingdomType findByName(String name, Context context) {
        return Arrays.stream(KingdomType.values())
                .collect(Collectors.toMap(t -> t, t -> context.getString(t.stringId)))
                .entrySet()
                .stream()
                .filter(s -> s.getValue().equals(name))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(KingdomType.NO);
    }



}
