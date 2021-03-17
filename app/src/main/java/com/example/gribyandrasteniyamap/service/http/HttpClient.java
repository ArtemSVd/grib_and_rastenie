package com.example.gribyandrasteniyamap.service.http;

import android.util.Base64;
import android.util.Log;

import com.example.gribyandrasteniyamap.view.model.User;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
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
        Request request = new Request.Builder()
                .header("Cookie", getCookies())
                .url(url)
                .build();

        try {
            return getHttpClient().newCall(request).execute();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
    }

    public Response postHttpMultipartResponse(String url, byte[] content, List<File> files) throws IOException {
        RequestBody qq = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), content);

        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("json", "json", qq);

        for (File file : files) {
            String contentType = file.toURL().openConnection().getContentType();
            RequestBody fileBody = RequestBody.create(MediaType.parse(contentType), file);
            builder.addFormDataPart("image", file.getName(), fileBody);
        }

        MultipartBody requestBody = builder.build();

        Request request = new Request.Builder()
                .header("Cookie", getCookies())
                .url(url)
                .post(requestBody)
                .build();

        try {
            return getHttpClient().newCall(request).execute();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
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
        stringBuilder.append(String.format("%s=%s", DEVICENUMBER_COOKIE, user.getDeviceName()));
        return stringBuilder.toString();
    }
}
