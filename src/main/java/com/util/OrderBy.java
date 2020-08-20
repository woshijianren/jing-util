package com.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderBy {

    /**
     * 排序字段名
     */
    private String fieldName;
    /**
     * 是否升序
     */
    private Boolean asc;
}
