package com.example.gribyandrasteniyamap.service.http;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.annotation.Nullable;

import com.example.gribyandrasteniyamap.dto.Plant;
import com.example.gribyandrasteniyamap.dto.PlantsRequestParams;
import com.example.gribyandrasteniyamap.util.SerializeUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import okhttp3.Response;

public class PlantHttpClient {
    private final HttpClient httpClient = new HttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    @Nullable
    public Bitmap getImage(String filePath) throws IOException {
        // todo: вынести ip+port в конфиг
        Response response = httpClient.getHttpResponse("http://172.22.206.1:8080/api/plants/" + filePath);
        InputStream inputStream = getBodyResponse(response);
        if (inputStream != null) {
            return BitmapFactory.decodeStream(inputStream);
        }
        return null;
    }

    public List<Plant> getPlants(PlantsRequestParams params) throws IOException {
        Response response = httpClient.postHttpResponse("http://172.22.206.1:8080/api/plants/list", SerializeUtil.getBytes(params));
        InputStream inputStream = getBodyResponse(response);
        if (inputStream != null) {
            return mapper.readValue(inputStream, mapper.getTypeFactory().constructCollectionType(List.class, Plant.class));
        }
        return null;
    }

    @Nullable
    private InputStream getBodyResponse(Response response) throws IOException {
        if (response != null && response.body() != null) {
            if (response.isSuccessful()) {
                return response.body().byteStream();
            } else {
                throw new IOException();
            }
        }
        return null;
    }


}
