package com.exception.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderBy {

    private String fieldName; //排序字段名
    private Boolean asc; //是否升序
}
