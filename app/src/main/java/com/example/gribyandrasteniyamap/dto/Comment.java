package com.example.gribyandrasteniyamap.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Comment {
    private Integer plantId;
    private String text;
    private LocalDateTime createdDate;
    private User user;
}
