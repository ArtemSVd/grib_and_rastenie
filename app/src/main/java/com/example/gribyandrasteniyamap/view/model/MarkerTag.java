package com.example.gribyandrasteniyamap.view.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class MarkerTag {
    private Long plantId;
    private boolean isLocal;
}
