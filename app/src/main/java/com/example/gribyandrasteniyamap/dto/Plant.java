package com.example.gribyandrasteniyamap.dto;


import com.example.gribyandrasteniyamap.KingdomType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Plant {
    private Integer id;

    private String fileName;

    private String filePath;

    private String name;

    private String description;

    private KingdomType type;

    private User user;

    private String coordinate;

    private List<Comment> comments;
}
