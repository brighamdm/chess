package handler;

import com.google.gson.Gson;

public interface Handler {

    Gson gson = new Gson();

    default <T> String toJson(T object) {
        return gson.toJson(object);
    }

    default <T> T fromJson(String json, Class<T> classType) {
        return gson.fromJson(json, classType);
    }
}
