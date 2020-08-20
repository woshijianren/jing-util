package com.annotation;

import org.apache.poi.ss.usermodel.HorizontalAlignment;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zyl
 * @date 2020/7/15 15:14
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface XSSF {

    /**
     * 数据的对齐方式，默认右对齐，表头不参与对齐，表头都是中对齐
     */
    HorizontalAlignment align() default HorizontalAlignment.RIGHT;

    /**
     * index对应的是改字段在excel表格中列的位置，从0开始
     * 但其实只要你没有重复的，并且大小无错，对该属性的处理会排序好的
     * 推荐10递增，更大也可以，防止后期需求改变或者自己将header设置错误，增加容错率
     */
    int index();

    /**
     * 对应excel的表头
     */
    String header();

    /**
     * 单位枚举，转换用，看具体情况，用枚举或者字符串
     */


}
