package com.test;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test {

//    public static void main(String[] args) {
//        String json = "{\"a\":\"\"}";
//        Gson gson = new Gson();
//        JsonObject s = gson.fromJson(json, JsonObject.class);
//        System.out.println(s.toString());
////        JsonElement a = null;
//        System.out.println(s.get("b").isJsonNull());
//    }
    private Map<Integer, String> map = new HashMap<>();

    public Map<Integer, String> getMap() {
        return map;
    }

//    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//        Test test = new Test();
//        test.getMap().put(3, "a");
//        Method getMap = Test.class.getDeclaredMethod("getMap");
//        Object invoke = getMap.invoke(test);
//        if (invoke instanceof Map) {
//            System.out.println(((Map) invoke).keySet());
//        }
//        System.out.println(test instanceof List<?>);
//        System.out.println(test instanceof List<Test>);
//        System.out.println((List<Test>) test);
////        System.out.println(test instanceof String);
//    }
}
