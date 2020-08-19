package com.exception.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author zyl
 * @date 2020/6/24 10:23
 */
public class OrderByUtil {

    public static List<OrderBy> composeOrder(OrderBy... orderBy) {
        List<OrderBy> orderByList = new ArrayList<>();
        Collections.addAll(orderByList, orderBy);
        return orderByList;
    }
}
