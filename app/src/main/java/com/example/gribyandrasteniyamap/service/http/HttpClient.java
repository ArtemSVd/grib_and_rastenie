package com.example.gribyandrasteniyamap.service.http;

import android.os.StrictMode;
import android.util.Log;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

public class HttpClient {

    public Response getHttpResponse(String url) {
        OkHttpClient httpClient = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        try {
            return  httpClient.newCall(request).execute();
        } catch (IOException e) {
            Log.e(TAG, "error in getting response get request okhttp");
        }
        return null;
    }

    public Response postHttpResponse(String url, byte[] content) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), content);
        OkHttpClient httpClient = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        try {
            return httpClient.newCall(request).execute();
        } catch (IOException e) {
            Log.e(TAG, "error in getting response post request okhttp");
        }
        return null;
    }
}
