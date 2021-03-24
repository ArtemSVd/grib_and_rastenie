package com.example.gribyandrasteniyamap.service.http;

import androidx.annotation.Nullable;

import com.example.gribyandrasteniyamap.dto.CommentDto;
import com.example.gribyandrasteniyamap.dto.PlantDto;
import com.example.gribyandrasteniyamap.dto.PlantsRequestParams;
import com.example.gribyandrasteniyamap.utils.SerializeUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.Response;

@Singleton
public class PlantsAppClient {
    @Inject
    HttpClient httpClient;

    private final ObjectMapper mapper;

    @Inject
    PlantsAppClient() {
        mapper = new ObjectMapper();
    }

    private final String PLANT_URL = "http://172.22.206.1:8080";

    public Map<Integer, Long> load(List<PlantDto> plants, List<File> files) throws IOException {
        Response response = httpClient.postHttpMultipartResponse(PLANT_URL + "/api/plants/upload", SerializeUtil.getBytes(plants), files);
        InputStream inputStream = getBodyResponse(response);
        if (inputStream != null) {
            TypeReference<HashMap<Integer, Long>> typeRef
                    = new TypeReference<HashMap<Integer, Long>>() {};
            return mapper.readValue(inputStream, typeRef);
        }
        return Collections.emptyMap();
    }

    public Boolean checkAvailable() throws IOException {
        Response response = httpClient.getHttpResponse(PLANT_URL + "/api/plants/available");
        InputStream inputStream = getBodyResponse(response);
        if (inputStream != null) {
            return mapper.readValue(inputStream, Boolean.class);
        }
        return false;
    }

    public List<PlantDto> getPlantsFromServer(PlantsRequestParams params) throws IOException {
        Response response = httpClient.postHttpResponse(PLANT_URL + "/api/plants/list", SerializeUtil.getBytes(params));
        InputStream inputStream = getBodyResponse(response);
        if (inputStream != null) {
            return mapper.readValue(inputStream, mapper.getTypeFactory().constructCollectionType(List.class, PlantDto.class));
        }
        return Collections.emptyList();
    }

    public PlantDto getPlantFromServer(Long plantId) throws IOException {
        Response response = httpClient.getHttpResponse(PLANT_URL+ "/api/plants/item/" + plantId);
        InputStream inputStream = getBodyResponse(response);
        if (inputStream != null) {
            return mapper.readValue(inputStream, PlantDto.class);
        }
        return null;
    }

    public List<CommentDto> getCommentsFromServer(Long plantId) throws IOException {
        Response response = httpClient.getHttpResponse(PLANT_URL + "/api/comment/plant/" + plantId);
        InputStream inputStream = getBodyResponse(response);
        if (inputStream != null) {
            return mapper.readValue(inputStream, mapper.getTypeFactory().constructCollectionType(List.class, CommentDto.class));
        }
        return Collections.emptyList();
    }

    public CommentDto addComment(CommentDto comment) throws IOException {
        Response response = httpClient.postHttpResponse(PLANT_URL + "/api/comment", SerializeUtil.getBytes(comment));
        InputStream inputStream = getBodyResponse(response);
        if (inputStream != null) {
            return mapper.readValue(inputStream, CommentDto.class);
        }
        return null;
    }

    @Nullable
    private InputStream getBodyResponse(Response response) {
        if (response != null && response.body() != null) {
            if (response.isSuccessful()) {
                return response.body().byteStream();
            }
        }
        return null;
    }
}
