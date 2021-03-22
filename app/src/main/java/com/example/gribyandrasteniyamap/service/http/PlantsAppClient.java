package com.example.gribyandrasteniyamap.service.http;

import androidx.annotation.Nullable;

import com.example.gribyandrasteniyamap.dto.PlantDto;
import com.example.gribyandrasteniyamap.dto.PlantsRequestParams;
import com.example.gribyandrasteniyamap.utils.SerializeUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import okhttp3.Response;

public class PlantsAppClient {
    @Inject
    HttpClient httpClient;

    @Inject
    PlantsAppClient() {
    }

    private final ObjectMapper mapper = new ObjectMapper();
    private final String PLANT_URL = "http://172.22.206.1:8080";

    public List<Integer> load(List<PlantDto> plants, List<File> files) throws IOException {
        Response response = httpClient.postHttpMultipartResponse(PLANT_URL + "/api/plants/upload", SerializeUtil.getBytes(plants), files);
        InputStream inputStream = getBodyResponse(response);
        if (inputStream != null) {
            return mapper.readValue(inputStream, mapper.getTypeFactory().constructCollectionType(List.class, Integer.class));
        }
        return Collections.emptyList();
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
