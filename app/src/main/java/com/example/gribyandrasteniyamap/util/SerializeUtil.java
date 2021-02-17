package com.example.gribyandrasteniyamap.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SerializeUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static byte[] getBytes(Object obj) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            objectMapper.writeValue(outputStream, obj);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return outputStream.toByteArray();
    }

}
