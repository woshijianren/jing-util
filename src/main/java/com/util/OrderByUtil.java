package com.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author zyl
 * @date 2020/6/24 10:23
 */
public class OrderByUtil {

    /**
     * 组合OrderBy，避免List的操作
     */
    public static List<OrderBy> composeOrder(OrderBy... orderBy) {
        List<OrderBy> orderByList = new ArrayList<>();
        Collections.addAll(orderByList, orderBy);
        return orderByList;
    }
}
