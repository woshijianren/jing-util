package com.exception.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimpleQuery {

    private String moduleInfoId;			//实体apiname
    private String recordType;
    private String viewCode;					//默认填"all"全部视图
    private List<Filter> filters;   // 可以为null，但是不能参数错误
    private List<OrderBy> orders;   // 可以为null，但是不能参数错误
    private int pageSize;
    private int currentPage;

    // 查询的默认构造，查询全部数据返回一页结果
    public SimpleQuery(String moduleInfoId) {
        this.currentPage = 1;
        this.pageSize = 10000000;
        this.viewCode = "all";
        this.moduleInfoId = moduleInfoId;
    }
}
