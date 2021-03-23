package com.example.gribyandrasteniyamap.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SerializeUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static byte[] getBytes(Object obj) {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            objectMapper.writeValue(outputStream, obj);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return outputStream.toByteArray();
    }

}
