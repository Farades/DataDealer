package ru.entel.smiu.datadealer.utils;

import com.google.gson.Gson;

/**
 * Created by farades on 27.08.15.
 */
public class JSONUtils {
    private static final Gson gson = new Gson();

    private JSONUtils(){}

    public static boolean isJSONValid(String JSON_STRING) {
        try {
            gson.fromJson(JSON_STRING, Object.class);
            return true;
        } catch(com.google.gson.JsonSyntaxException ex) {
            return false;
        }
    }
}
