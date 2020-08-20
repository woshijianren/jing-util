//package com.util;
//
//import cn.hutool.core.date.DateUnit;
//import cn.hutool.core.date.DateUtil;
//import cn.hutool.core.util.StrUtil;
//import cn.hutool.setting.Setting;
//import com.bzua.jingdata.service.AssetsLiabilitiesTableService;
//import com.google.gson.Gson;
//import com.google.gson.JsonArray;
//import com.google.gson.JsonElement;
//import com.google.gson.JsonObject;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//import org.springframework.util.DigestUtils;
//
//import java.math.BigDecimal;
//import java.net.InetAddress;
//import java.net.UnknownHostException;
//import java.util.*;
//
///**
// * @author zyl
// * @date 2020/7/31 9:04
// * @decribe 获取股价的定时器，股价按需求根据时间乘以了对应的历史汇率和对应的实时汇率后存入properties文件
// */
//@Component
//@Slf4j
//public class StockPriceTask {
//    // 股价接口
//    @Value("${stock.price.url}")
//    String url = "";
//
//    // 实时汇率接口
//    @Value("${stock.price.ratio.effective}")
//    String effectiveRatioUrl = "";
//
//    // 历史汇率接口
//    @Value("${stock.price.ratio.history}")
//    String historyRatioUrl = "";
//
//    // 股价写入文件地址
//    @Value("${stock.price.file}")
//    String stockFileLocation = "";
//
//    // 获取股价时需要获取所有股票代码需要token
//    @Value("${stock.price.token}")
//    String stockPriceToken = "";
//
//    // 股价参数
//    @Value("${stock.price.serviceId}")
//    String serviceId = "";
//    // 股价参数
//    @Value("${stock.price.appkey}")
//    String appkey = "";
//    // 股价参数
//    @Value("${stock.price.appSecret}")
//    String appSecret = "";
//
//    @Autowired
//    private AssetsLiabilitiesTableService service;
//
//    @Scheduled(initialDelay = 2000, fixedRate = 43200000)
//    public void taskStockPrice() {
//        try {
//            Map<String, String> finalStockPriceMap = new LinkedHashMap<>();
//            JsonArray tableData = service.getTableData("company", stockPriceToken, FilterUtils.composeFilter(FilterUtils.notNullFilter("stock_code")), null);
//            for (JsonElement tableDatum : tableData) {
//                JsonObject datum = (JsonObject) tableDatum;
//                String stockCode = JsonUtil.getJsonString(datum, "stock_code");
//                String stockAfterAssemble = "";
//                log.info("拼装前的股票代码：" + stockCode);
//                if (StrUtil.endWithIgnoreCase(stockCode, ".HK")) {
//                    stockAfterAssemble = stockCode.substring(0, stockCode.lastIndexOf(".")) + ".HK";
//                } else if (StrUtil.length(stockCode) == 5) {
//                    stockAfterAssemble = stockCode + ".HK";
//                } else {
//                    stockAfterAssemble = stockCode;
//                }
//                log.info("拼装后的股票代码：" + stockAfterAssemble);
//                // 单个股票代码的历史股价
//                String stockPrice = getTrueStockPrice(stockAfterAssemble);
//                List<List<String>> ratioList = getRatioResult();
//                JsonObject jsonObject = new Gson().fromJson(stockPrice, JsonObject.class);
//                JsonElement data = jsonObject.get("data");
//                // 这里必须这么写，这里有点奇怪，{}
//                if ("{}".equals(data.toString())) {
//                    continue;
//                }
//                JsonObject stockPriceJsonObject = data.getAsJsonObject();
//                // 货币，一般取出来的是中文，而不是货币代码
//                String currency = JsonUtil.getJsonString(stockPriceJsonObject, "currency");
//                JsonObject list = stockPriceJsonObject.get("list").getAsJsonObject();
//                JsonArray days = list.getAsJsonArray("days");
//                // 最晚是哪天才有股价
//                finalStockPriceMap.put(stockCode, days.get(days.size() - 1).getAsString());
//                // 最早的哪天才有股价记录
//                finalStockPriceMap.put(stockCode + "beginDay", days.get(0).getAsString());
//                JsonArray values = list.get("values").getAsJsonArray();
//                // 股价的时间和价格的map集合
//                Map<String, String> stockPriceMap = new LinkedHashMap<>();
//                for (int i = 0; i < days.size(); i++) {
//                    String stockDay = days.get(i).getAsString();
//                    String initStockPrice = values.get(i).getAsJsonArray().get(1).getAsString();
//                    // 股价时间+股票代码的key
//                    String key = stockDay + stockCode;
//                    // 数据里面的占位是用的”-“
//                    if ("-".equals(initStockPrice) || StrUtil.isBlank(initStockPrice)) {
//                        // 将股价map中所有非数字替换为1.00，即无股价
//                        log.info("没有股价,设置为1.00;股票代码：{}；货币代码：{}；股价时间：{}；股价：{}", stockCode, currency, stockDay, initStockPrice);
//                        stockPriceMap.put(key, "1.00");
//                        continue;
//                    }
//                    // 是否进行了汇率相乘，如果没有对应时间的汇率，那么就存储原来的值
//                    boolean isStore = false;
//                    for (List<String> stringList : ratioList) {
//                        // 货币代码
//                        String currencyCode = stringList.get(0);
//                        // 货币中文名称
//                        String currencyChinese = stringList.get(1);
//                        // 截取时间，正常格式截取之后就是yyyy-MM-dd的
//                        String s = StrUtil.subWithLength(key, 0, 10);
//                        // 截取到的股价的时间的毫秒值
//                        long mills = DateUtil.parseDate(s).getTime();
//                        // 如果股价不是其他占位符，只是数字，那么他就是一个股价，要乘以对应的汇率了，
//                        BigDecimal ratio = new BigDecimal(stringList.get(2));
//                        // 乘以汇率之后的股价
//                        String stockPriceAfterRatio = String.valueOf(new BigDecimal(initStockPrice).multiply(ratio));
//                        // 如果货币对的上，就要判断汇率有效时间来乘以对应的汇率
//                        if (currencyCode.equals(currency) || currencyChinese.equals(currency)) {
//                            // 下标4是汇率过期时间，如果是实时汇率，那么这里是null的
//                            if (StrUtil.isNullOrUndefined(stringList.get(4))) {
//                                // mills是股价时间， 后者是汇率生效时间
//                                if (mills >= Long.parseLong(stringList.get(3))) {
//                                    Date date = new Date();
//                                    date.setTime(Long.parseLong(stringList.get(3)));
//                                    log.info("实时汇率——股票代码：{}；货币代码：{}；股价时间：{}；汇率生效时间：{}；原生股价：{}；汇率：{}；汇率后的股价：{}", stockCode, currency, DateUtil.parseDate(s), DateUtil.format(date, "yyyy-MM-dd HH:mm:ss"), initStockPrice, ratio, stockPriceAfterRatio);
//                                    isStore = true;
//                                    stockPriceMap.put(key, stockPriceAfterRatio);
//                                    break;
//                                }
//                            } else {
//                                // 需要股价的时间在两个时间范围内
//                                if (mills >= Long.parseLong(stringList.get(3)) && mills <= Long.parseLong(stringList.get(4))) {
//                                    Date starTime = new Date();
//                                    starTime.setTime(Long.parseLong(stringList.get(3)));
//                                    Date endTime = new Date();
//                                    endTime.setTime(Long.parseLong(stringList.get(4)));
//                                    log.info("过期汇率——股票代码：{}；货币代码：{}；股价时间：{}；汇率开始时间：{}；汇率结束时间：{}；原生股价：{}；汇率：{}；汇率后的股价：{}；", stockCode, currency, DateUtil.parseDate(s), DateUtil.format(starTime, "yyyy-MM-dd HH:mm:ss"), DateUtil.format(endTime, "yyyy-MM-dd HH:mm:ss"), initStockPrice, ratio, stockPriceAfterRatio);
//                                    isStore = true;
//                                    stockPriceMap.put(key, stockPriceAfterRatio);
//                                    break;
//                                }
//                            }
//                        }
//                    }
//                    if (!isStore) {
//                        log.info("没有找到对应股价时间的汇率；股票代码：{}；货币代码：{}；原始股价：{}；股价时间：{}；", stockCode, currency, initStockPrice, stockDay);
//                        stockPriceMap.put(key, initStockPrice);
//                    }
//                }
//                finalStockPriceMap.putAll(stockPriceMap);
//            }
//            writeProperties(finalStockPriceMap);
//        } catch (Exception e) {
//            log.error("股价获取失败,{}", e);
//        }
//
//    }
//
//    /**
//     * 获取股价，那天没有股价，就会继续往前一日找
//     *
//     * @param stockCode 股票代码
//     * @param day       时间，哪天的股价需要获取，格式：yyyy-MM-dd
//     * @return 股价
//     */
//    public BigDecimal getStockPrice(String stockCode, String day) {
//        // 没有股价的话
//        BigDecimal noStockPrice = new BigDecimal("1.00");
//        if (Constant.NULL_STRING == stockCode) {
//            return noStockPrice;
//        }
//        // 最晚的股价时间
//        String newestDay = getPropValue(stockCode);
//        log.info("股价：最晚的股价时间：" + newestDay + ".---------------------------------------------");
//        // 最早的股价时间
//        String earliest = getPropValue(stockCode + "beginDay");
//        if (newestDay == null) {
//            return noStockPrice;
//        }
//        log.info("股价：最早的股价时间：" + newestDay + ".---------------------------------------------");
//        // 如果最早的股价在所要查询的时间之后就直接返回0
//        if ((DateUtil.between(DateUtil.parseDate(earliest), DateUtil.parse(day), DateUnit.DAY, false) < 0)) {
//            return noStockPrice;
//        }
//        // 如果最新的股价时间小于day这个时间，就说明往前很长一段时间都没有股价，因此需要直接从newestDay开始获取股价
//        if (DateUtil.between(DateUtil.parseDate(newestDay), DateUtil.parse(day), DateUnit.DAY, false) > 0) {
//            day = newestDay;
//        }
//        // 取股价的时间，这个时间没有股价就继续往前推一天取
//        // 最早的股价时间和查询的时间之间有多少天，最大可能查询这么多次
//        log.info("股价：开始获取股价时间，准备读取股价价格-------------------------------------------");
//        long between = DateUtil.between(DateUtil.parse(earliest, "yyyy-MM-dd"), DateUtil.parse(day), DateUnit.DAY, true);
//        List<String> stockTime = new ArrayList<>();
//        for (int i = 0; i < between; i ++) {
//            stockTime.add(day + stockCode);
//            day = DateUtil.formatDate(DateUtil.offsetDay(DateUtil.parse(day, "yyyy-MM-dd"), -1));
//        }
//        log.info("股价：开始读取股价价格---------------------------------------------------------");
//        log.info("股价：股价已读取完毕----------------------------------------------------------------");
//        return getPropValue(stockTime);
//    }
//
//    public BigDecimal getPropValue(List<String> propKey) {
//        Setting setting = new Setting(stockFileLocation);
//        for (String s : propKey) {
//            log.info("开始从本地文件中读取股价--------------------------------------------------------");
//            log.info("取值：" + s);
//            String property = setting.get(s);
//            if (property != null && !"-".equals(property)) {
//                log.info("股价：" + property);
//                return new BigDecimal(property);
//            }
//        }
//        return new BigDecimal("1.00");
//    }
//
//    /**
//     * 获取properties属性值
//     *
//     * @param propKey
//     * @return
//     */
//    public String getPropValue(String propKey) {
//        Setting setting = new Setting(stockFileLocation);
//        log.info("股价：股价文件加载结束");
//        return setting.get(propKey);
//    }
//
//    /**
//     * 获取所有汇率有用到的数据放入集合，需要注意的是，如果version是最新版本，那么这个”失效时间“会是null
//     *
//     * @param resultString
//     * @return
//     */
//    private List<List<String>> getRatioResult(String resultString) {
//        List<List<String>> finalResult = new ArrayList<>();
//        JsonObject jsonObject = new Gson().fromJson(resultString, JsonObject.class);
//        // 获取版本，之后要用到他来获取过期汇率
//        JsonArray result = jsonObject.getAsJsonArray("result");
//        for (JsonElement jsonElement : result) {
//            List<String> list = new ArrayList<>();
//            JsonObject data = (JsonObject) jsonElement;
//            // 货币英文代码
//            list.add(JsonUtil.getJsonString(data, "currency"));
//            // 货币中文代码
//            list.add(JsonUtil.getJsonString(data, "label"));
//            // 汇率
//            list.add(JsonUtil.getJsonString(data, "currencyRate"));
//            // 获取生效时间，毫秒值
//            list.add(JsonUtil.getJsonString(data, "effectiveDate"));
//            // 失效时间,如果是实时汇率，那就是null
//            list.add(JsonUtil.getJsonString(data, "failureDate"));
//            finalResult.add(list);
//        }
//        return finalResult;
//    }
//
//    private void writeProperties(Map<String, String> map) {
//        Setting setting = new Setting();
//        for (Map.Entry<String, String> entry : map.entrySet()) {
//            setting.set(entry.getKey(), entry.getValue());
//        }
//        log.info("写入股价进入文件————————————————————————————————————————————————");
//        setting.store(stockFileLocation);
//        log.info("写入股价成功————————————————————————————————————————————————————");
//    }
//
//    /**
//     * 根据股票代码获取股价，返回结果是整个Json对象的字符串
//     *
//     * @param stockCode
//     * @return
//     */
//    public String getTrueStockPrice(String stockCode) {
//        String ip = null;
//        try {
//            ip = InetAddress.getLocalHost().getHostAddress();
//        } catch (UnknownHostException e) {
//            log.error("{}", e);
//        }
//
//        Map<String, String> headerMap = new HashMap<>();
//        String time = String.valueOf(System.currentTimeMillis());
//        headerMap.put("timestamp", time);
//        headerMap.put("serviceid", serviceId);
//        headerMap.put("appkey", appkey);
//        headerMap.put("remote_ip", ip);
//        Map<String, String> sParaNew = paraFilter(headerMap);
//        //获取待签名字符串
//        String preSignStr = createLinkString(sParaNew) + "&" + "appSecret" + "=" + appSecret;
//        // log.info("签名前字符串为->" + preSignStr);
//        Map<String, String> header = new HashMap<>();
//        header.put("remote_ip", ip);
//        // log.info("[sign]-->"+MD5Utils.getMd5String(preSignStr));
//        header.put("sign", DigestUtils.md5DigestAsHex(preSignStr.getBytes()));
//        header.put("timestamp", time);
//        header.put("serviceid", serviceId);
//        header.put("appkey", appkey);
//
//        Map<String, String> paramsMap = new HashMap<>();
//        paramsMap.put("stockCode", stockCode);
//        paramsMap.put("curPage", "1");
//        paramsMap.put("pageSize", "20000");
//        log.info("股价：开始对股票代码{}发起请求-------------------------------------", stockCode);
//        try {
//            return HttpClientUtil.doGetRequest(url, header, paramsMap);
//        } catch (Exception e) {
//            log.error("获取股价失败，{}", e);
//        }
//        log.info("股价：对股票代码{}发起请求结束-------------------------------------", stockCode);
//        return null;
//    }
//
//
//    /**
//     * 得到所有的汇率相关信息，全部放入List，然后用List存起来
//     *
//     * @return
//     */
//    private List<List<String>> getRatioResult() {
//        List<List<String>> finalResult = new ArrayList<>();
//        String effectiveRatioString = getEffectiveRatioString();
//        JsonObject jsonObject = new Gson().fromJson(effectiveRatioString, JsonObject.class);
//        int version = jsonObject.getAsJsonArray("result").get(0).getAsJsonObject().get("version").getAsInt();
//        for (int i = 0; i <= version; i++) {
//            String result = requestAllRatio(i);
//            List<List<String>> ratioResult = getRatioResult(result);
//            finalResult.addAll(ratioResult);
//        }
//        return finalResult;
//    }
//
//    /**
//     * 得到实时汇率的Json数据的String类型
//     *
//     * @return
//     */
//    private String getEffectiveRatioString() {
//        Map<String, String> headerMap = new HashMap<>();
//        headerMap.put("Cookie", "saas_login_token=" + stockPriceToken);
//        headerMap.put("Content-type", "application/json");
//        String resultString = null;
//        try {
//            resultString = HttpClientUtil.doPostJsonRequest(effectiveRatioUrl, headerMap, "{\"currentPage\":1,\"pageSize\":30}");
//        } catch (Exception e) {
//            log.error("获取汇率失败，{}", e);
//        }
//        return resultString;
//    }
//
//    /**
//     * 得到过期汇率的Json的字符串
//     *
//     * @param version 版本号，根据当前汇率的version来减1，最小值为1
//     * @return
//     */
//    private String requestAllRatio(Integer version) {
//        Map<String, String> headerMap = new HashMap<>();
//        headerMap.put("Cookie", "saas_login_token=" + stockPriceToken);
//        headerMap.put("Content-type", "application/json");
//        String resultString = null;
//        try {
//            resultString = HttpClientUtil.doPostJsonRequest(effectiveRatioUrl, headerMap, "{\"currentPage\":1,\"pageSize\":30,\"version\":}" + version);
//        } catch (Exception e) {
//            log.error("获取汇率失败，{}", e);
//        }
//        return resultString;
//    }
//
//
//    private Map<String, String> paraFilter(Map<String, String> sArray) {
//        Map<String, String> result = new HashMap<String, String>();
//        if (sArray == null || sArray.size() <= 0) {
//            return result;
//        }
//        for (String key : sArray.keySet()) {
//            String value = sArray.get(key);
//            if (value == null || "".equals(value) || "sign".equalsIgnoreCase(key)
//                    || "sign_type".equalsIgnoreCase(key)) {
//                continue;
//            }
//            result.put(key, value);
//        }
//        return result;
//    }
//
//    private String createLinkString(Map<String, String> params) {
//        List<String> keys = new ArrayList<String>(params.keySet());
//        Collections.sort(keys);
//        String prestr = "";
//        for (int i = 0; i < keys.size(); i++) {
//            String key = keys.get(i);
//            String value = params.get(key);
//            if (i == keys.size() - 1) {//拼接时，不包括最后一个&字符
//                prestr = prestr + key + "=" + value;
//            } else {
//                prestr = prestr + key + "=" + value + "&";
//            }
//        }
//        return prestr;
//    }
//}
