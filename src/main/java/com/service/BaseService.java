package com.service;

import com.exception.BadRequestException;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.util.Filter;
import com.util.HttpClientUtil;
import com.util.OrderBy;
import com.util.SimpleQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;

/**
 * @author zyl
 * @date 2020/6/20 16:43
 */
@Slf4j
public abstract class BaseService {

    @Value("${data.url}")
    private String dataUrl;

    /**
     * 将获取数据的方法进一步封装，直接一步到位取出原数据的结果
     *
     * @param moduleInfoId     业务对象的apiName
     * @param userToken        token，saas_login_token=xxx中的xxx
     * @param conditionFilters 条件过滤器，不需要则传null
     * @param orderByList      排序，不需要则传null
     * @return /
     */
    public JsonArray getTableData(String moduleInfoId, String userToken, List<Filter> conditionFilters, List<OrderBy> orderByList) {
        String cookie = spliceCookie(userToken);
        return getTableData(getData(moduleInfoId, cookie, conditionFilters, orderByList));
    }

    public String spliceCookie(String userToken) {
        return "saas_login_token=" + userToken;
    }

    /**
     * 拼接超链
     *
     * @param mouldInfoId 业务对象的apiName
     * @param projectName 显示的名称
     * @param id          跳转数据的id
     * @return <a href="/#/……"></a>
     */
    public String spliceHref(String mouldInfoId, String projectName, String id) {
        return "<a href=\"/#/md/module/" + mouldInfoId + "/edit/" + id + "\" style=\"color:#7080eb;text-decoration:none;\" target=\"_blank\">" + projectName + "</a>";
    }

    /**
     * 获取返回数据中的tableData中的数据
     */
    private JsonArray getTableData(String resultData) {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(resultData, JsonObject.class);
        int resultCode = jsonObject.get("code").getAsInt();
        if (resultCode == 0) {
            JsonObject resultArray = jsonObject.getAsJsonObject("result");
            if (resultArray.get("tableData").isJsonNull()) {
                return new JsonArray();
            }
            log.info("最终结果：" + resultArray.getAsJsonArray("tableData").toString());
            return resultArray.getAsJsonArray("tableData");
        }
        throw new BadRequestException("请求数据失败，请稍后重试");
    }

    /**
     * 得到请求后返回的全部数据
     *
     * @param moduleInfoId     apiName
     * @param cookie           /
     * @param conditionFilters 过滤条件
     * @param orderByList      排序
     * @return BaseReturn.class的getReturnMessage()的结果
     */
    private String getData(String moduleInfoId, String cookie, List<Filter> conditionFilters, List<OrderBy> orderByList) {
        SimpleQuery query = new SimpleQuery(moduleInfoId);
        query.setModuleInfoId(moduleInfoId);
        query.setOrders(orderByList);
        query.setFilters(conditionFilters);
        return getData(query, cookie);
    }

    private String getData(SimpleQuery query, String cookie) {
        Gson gson = new Gson();
        String str = gson.toJson(query);
        Map<String, String> headParams = new HashMap<>();
        headParams.put("Cookie", cookie);
        headParams.put("Content-type", "application/json");
        return HttpClientUtil.doPostJsonRequest(dataUrl, headParams, str);
    }

    public abstract List<?> calculationData(String userToken, String startDate, String endDate);
















    // 如果不需要可能会删除,但是先保留
    public String getProjectNameFromHref(String projectHref) {
        int index = projectHref.indexOf(">");
        int lastIndex = projectHref.lastIndexOf("<");
        return projectHref.substring(index + 1, lastIndex);
    }

    public Map<String, Object> reverseMap(LinkedHashMap<String, Object> map) {
        ListIterator<Map.Entry<String, Object>> i = new ArrayList<>(map.entrySet()).listIterator(map.size());
        LinkedHashMap<String, Object> linkedHashMap = new LinkedHashMap<>();
        while (i.hasPrevious()) {
            Map.Entry<String, Object> entry = i.previous();
            linkedHashMap.put(entry.getKey(), entry.getValue());
        }
        return linkedHashMap;
    }

}
