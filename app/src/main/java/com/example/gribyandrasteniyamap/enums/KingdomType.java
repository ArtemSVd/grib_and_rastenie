package com.example.gribyandrasteniyamap.enums;

import android.content.Context;

import com.example.gribyandrasteniyamap.R;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum KingdomType {
    NO(0, R.string.kingdomType_no),
    MUSHROOMS(1, R.string.kingdomType_mushrooms),
    PLANT(2, R.string.kingdomType_plants);

    private final int id;
    private final int stringId;

    public static List<String> nameValues(Context context) {
        return nameValues(context, Collections.emptyList());
    }

    public static List<String> nameValues(Context context, List<Integer> excludeIds) {
        return Arrays.stream(KingdomType.values())
                .filter(v -> !excludeIds.contains(v.id))
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

    public static KingdomType findById(int id) {
        return Arrays.stream(KingdomType.values())
                .filter(type -> type.getId() == id)
                .findFirst()
                .orElse(KingdomType.NO);
    }


}
