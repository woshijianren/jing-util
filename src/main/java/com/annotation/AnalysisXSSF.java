package com.annotation;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.*;
import java.util.*;


/**
 * @author zyl
 * @date 2020/8/20
 */
@Slf4j
public class AnalysisXSSF {

    /**
     * 根据传入的dataList解析其具体类型并获取该类中所有被@XSSF注解的get方法
     *
     * @param dataList List<?>{}内部类
     * @return 方法集合
     */
    static List<Method> getAllGetMethodsWithXSSF(List<?> dataList) throws NoSuchMethodException {
        Class<?> clazz = getListClass(dataList);
        List<Field> fieldList = getAllFieldWithXSSF(clazz);
        return getPartGetMethods(clazz, fieldList);
    }

    /**
     * 获取集合的具体类型
     * 必须传入一个内部类写法的List
     *
     * @param dataList 必须是内部类写法才能被解析，不然会出现转换异常
     * @return 集合的具体类型
     */
    static Class<?> getListClass(List<?> dataList) {
        Type type = dataList.getClass().getGenericSuperclass();
        return (Class<?>) ((ParameterizedType) type).getActualTypeArguments()[0];
    }

    /**
     * 得到某个类中的所有被@XSSF所注释的字段
     *
     * @param clazz 具体某个类
     * @return 字段集合
     */
    static List<Field> getAllFieldWithXSSF(Class<?> clazz) {
        Map<Integer, Field> fieldMap = new TreeMap<>(Comparator.comparingInt(o -> o));
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            if (declaredField.isAnnotationPresent(XSSF.class)) {
                XSSF xssf = declaredField.getDeclaredAnnotation(XSSF.class);
                fieldMap.put(xssf.index(), declaredField);
            }
        }
        return new ArrayList<>(fieldMap.values());
    }

    /**
     * 获取某个类中某些指定字段的get方法
     */
    static List<Method> getPartGetMethods(Class<?> clazz, List<Field> fieldList) throws NoSuchMethodException {
        List<Method> methodList = new ArrayList<>();
        for (Field field : fieldList) {
            methodList.add(clazz.getDeclaredMethod(splicePrefixMethod("get", field, clazz)));
        }
        return methodList;
    }

    /**
     * 得到某个类中所有被@XSSF注解的字段的set方法
     */
    public static List<Method> getAllSetMethodsWithXSSF(Class<?> clazz) throws NoSuchMethodException {
        List<Field> fieldList = getAllFieldWithXSSF(clazz);
        List<Method> methodList = new ArrayList<>();
        for (Field field : fieldList) {
            methodList.add(clazz.getDeclaredMethod(splicePrefixMethod("set", field, clazz), String.class));
        }
        return methodList;
    }

    /**
     * 拼接方法，一般都是前缀加字段名首字母大写
     *
     * @param prefix 前缀，一般只有：get和set这两个
     * @param field  字段名
     * @param clazz  类
     * @return setXxx或者getXxx
     */
    private static String splicePrefixMethod(String prefix, Field field, Class<?> clazz) {
        String fieldName = field.getName();
        prefix += fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        return prefix;
    }

}
