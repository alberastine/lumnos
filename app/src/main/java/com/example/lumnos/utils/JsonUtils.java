package com.example.lumnos.utils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class JsonUtils {
    private static final Gson gson = new Gson();

    public static <T> String toJson(T object) {
        return gson.toJson(object);
    }

    public static <T> T fromJson(String json, Type type) {
        if (json == null || json.isEmpty()) {
            // Return an empty list or a new object instance if the JSON is empty
            if (type.toString().contains("List")) {
                return (T) new ArrayList<>();
            }
            try {
                // This is a simplistic way to handle non-list types
                // A better implementation might use factories or reflection
                return gson.fromJson("{}", type);
            } catch (Exception e) {
                return null;
            }
        }
        try {
            return gson.fromJson(json, type);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            // Return a default value in case of parsing error
            if (type.toString().contains("List")) {
                return (T) new ArrayList<>();
            }
            return null;
        }
    }
}
