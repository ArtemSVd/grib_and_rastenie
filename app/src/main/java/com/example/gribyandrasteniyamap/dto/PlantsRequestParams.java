package com.example.gribyandrasteniyamap.dto;

import com.example.gribyandrasteniyamap.KingdomType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "kingdomType",
        "name"
})
public class PlantsRequestParams {
    @JsonProperty("kingdomType")
    private final KingdomType kingdomType;
    @JsonProperty("name")
    private final String name;
}
