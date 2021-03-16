package com.example.gribyandrasteniyamap.service.http;

import android.util.Base64;
import android.util.Log;

import com.example.gribyandrasteniyamap.view.model.User;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

public class HttpClient {

    @Inject
    User user;

    @Inject
    public HttpClient() {
    }

    private final String USERNAME_COOKIE = "user";
    private final String DEVICENUMBER_COOKIE = "device";

    public Response getHttpResponse(String url) {
        OkHttpClient httpClient = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        try {
            return httpClient.newCall(request).execute();
        } catch (IOException e) {
            Log.e(TAG, "error in getting response get request okhttp");
        }
        return null;
    }

    public Response postHttpResponse(String url, byte[] content) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), content);

        Request request = new Request.Builder()
                .header("Cookie", getCookies())
                .url(url)
                .post(requestBody)
                .build();

        try {
            return getHttpClient().newCall(request).execute();
        } catch (IOException e) {
            Log.e(TAG, "error in getting response post request okhttp");
            return null;
        }
    }

    private OkHttpClient getHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .build();
    }

    private String getCookies() {
        StringBuilder stringBuilder = new StringBuilder();
        if (user.getName() != null) {
            String encode = Base64.encodeToString(user.getName().getBytes(), Base64.NO_WRAP);
            stringBuilder.append(String.format("%s=%s", USERNAME_COOKIE, encode)).append(";");
        }
        stringBuilder.append(String.format("%s=%s", DEVICENUMBER_COOKIE, user.getDeviceNumber()));
        return stringBuilder.toString();
    }
}
