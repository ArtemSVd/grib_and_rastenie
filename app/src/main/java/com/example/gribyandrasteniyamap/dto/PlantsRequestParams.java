package com.example.gribyandrasteniyamap.dto;

import com.example.gribyandrasteniyamap.enums.KingdomType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "kingdomType",
        "name"
})
public class PlantsRequestParams {
    @JsonProperty("kingdomTypes")
    private final List<KingdomType> kingdomTypes;
    @JsonProperty("name")
    private final String name;
}