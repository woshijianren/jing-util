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
     * 推荐5递增，更大也可以，防止后期需求改变
     */
    int index();

    /**
     * 对应excel的表头
     */
    String header();

    /**
     * 是否金额类型，txt的导出需要取消三位分节法
     */
    boolean isMoney() default false;

    /*
        是否是数字，是的话，用0占位
     */
    boolean isNumber() default false;

    boolean isRatio() default false;

    boolean isCNY() default false;
}
