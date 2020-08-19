package com.exception.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Filter {
    private String fieldName; //字段名
    private Operator operator; //运算符（一般为 in ）
    private List<String> fieldValues; //字段筛选值（一般为数组）
}
