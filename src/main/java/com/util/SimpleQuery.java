package com.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author zyl
 * @date 2020/8/20 11:00
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimpleQuery {

    /**
     * 实体，即业务对象的apiName
     */
    private String moduleInfoId;
    /**
     * 业务类型
     */
    private String recordType;
    /**
     * 视图代码，默认填"all"全部视图
     */
    private String viewCode;
    /**
     * 过滤器，可以为null，但是不能参数错误
     */
    private List<Filter> filters;

    /**
     * 排序，可以为null，但是不能参数错误
     */
    private List<OrderBy> orders;
    /**
     * 一页有多少条数据，一般是只查询一页，一页返回全部数据
     */
    private int pageSize;
    /**
     * 当前页，一般是只查询一页，一页返回全部数据
     */
    private int currentPage;

    public SimpleQuery(String moduleInfoId) {
        this.currentPage = 1;
        this.pageSize = 10000000;
        this.viewCode = "all";
        this.moduleInfoId = moduleInfoId;
    }
}
