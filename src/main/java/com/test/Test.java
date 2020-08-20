package com.test;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Test {

    public static void main(String[] args) {
        String json = "{\"a\":\"\"}";
        Gson gson = new Gson();
        JsonObject s = gson.fromJson(json, JsonObject.class);
        System.out.println(s.toString());
//        JsonElement a = null;
        System.out.println(s.get("b").isJsonNull());
    }
}
