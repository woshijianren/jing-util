//package com.exception.util;
//
//import cn.hutool.core.util.StrUtil;
//import cn.hutool.core.util.ZipUtil;
//import com.google.gson.Gson;
//import com.google.gson.JsonArray;
//import com.google.gson.JsonElement;
//import com.google.gson.JsonObject;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.http.HttpResponse;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClients;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import java.io.File;
//import java.io.InputStream;
//import java.util.*;
//
//@Component
//@Slf4j
//public class JZFileUtil {
//
//    /**
//     * 说明：
//     * 要拿到JZ的物理文件需要三步
//     * 1、包装query json，调用file query接口，拿到文件的软连接，https://zjzb.jingdata.com/api/metadata/file/query
//     * 2、用软连接和下载类型，拼装数据调用下载接口，拿到token，https://zjzb.jingdata.com/api/file/preview/downloadPath
//     * 3、根据token和软连接，拿到下载地址：https://zjzb.jingdata.com/api/file/store/downloadFileToken?token=ca429d94d6cfd23d502e0a3181e32e91&fileName=CISP-10993001_A1032_V01_1_20200701_12_Q.zip&storePath=/110/118/987e83a8-2f95-4bd1-85c0-22675c50e68a_-1444596877.zip
//     */
//
//    public static List<ProductFileInformation> queryFileList(String queryFileUrl, String dataId, String moduleInfoId, String userCookie) throws Exception {
//        String dataIdChar = "###DATAID###";
//        String moduleInfoIdChar = "###MODULEINFOID###";
//        List<ProductFileInformation> fileList = new ArrayList<>();
//        String queryJson = "{\n" +
//                "  \"currentPage\": 1,\n" +
//                "  \"dataId\": \"###DATAID###\",\n" +
//                "  \"fileTypeList\": [\"other__u\"],\n" +
//                "  \"key\": \"\",\n" +
//                "  \"obj\": {\n" +
//                "    \"label\": \"文件区域组件\",\n" +
//                "    \"meta\": {\n" +
//                "      \"innerName\": \"file\",\n" +
//                "      \"key\": \"rightFile\",\n" +
//                "      \"label\": \"文件区域组件\"\n" +
//                "    },\n" +
//                "    \"random\": \"wyqbdsotfvmrchsp\",\n" +
//                "    \"remark\": \"文件内容\",\n" +
//                "    \"value\":{\n" +
//                "      \"fileType\":[\n" +
//                "        {\n" +
//                "          \"apiName\": \"other__u\",\n" +
//                "          \"category\": \"file\",\n" +
//                "          \"createdAt\": 1587893333772,\n" +
//                "          \"createdBy\": \"管理员\",\n" +
//                "          \"deleted\": false,\n" +
//                "          \"description\": null,\n" +
//                "          \"id\": \"5ea55455c05e84048e979201\",\n" +
//                "          \"label\": \"其他\",\n" +
//                "          \"lastModifiedAt\": 1587893333772,\n" +
//                "          \"lastModifiedBy\": \"管理员\",\n" +
//                "          \"menu\":[\n" +
//                "            {\"command\": \"edit\",\"label\": \"编辑\"}\n" +
//                "          ],\n" +
//                "          \"objectDescribeApiName\": \"Object_aumf__u\",\n" +
//                "          \"selected\": true,\n" +
//                "          \"tenantId\": \"5ea52c6fc05e8417815f2235\"\n" +
//                "        }\n" +
//                "      ],\n" +
//                "      \"orderBy\":null,\n" +
//                "      \"sort\":\"DESC\",\n" +
//                "      \"title\":\"文件\"\n" +
//                "    }\n" +
//                "  },\n" +
//                "  \"objectDescribeApiName\": \"###MODULEINFOID###\",\n" +
//                "  \"pageSize\": 10,\n" +
//                "  \"summary\": false\n" +
//                "}\n";
//
//        if(!StrUtil.isBlank(queryJson)) {
//            queryJson = queryJson.replaceAll(dataIdChar, dataId);
//            queryJson = queryJson.replaceAll(moduleInfoIdChar, moduleInfoId);
//            Map<String, String> header = new HashMap<>();
//            header.put("Cookie", userCookie);
//            header.put("Content-type", "application/json");
//            String result = HttpClientUtil.doPostJsonRequest(queryFileUrl, header, queryJson);
//            Gson gson = new Gson();
//            JsonObject resultObj = gson.fromJson(result, JsonObject.class);
//            if(resultObj.isJsonObject()) {
//                JsonArray arrayList = resultObj.get("result").getAsJsonObject().get("data").getAsJsonObject().get("tableData").getAsJsonArray().get(0).getAsJsonObject().get("list").getAsJsonArray();
//                for(JsonElement element : arrayList) {
//                    ProductFileInformation file = new ProductFileInformation();
//                    file.setStorePath(element.getAsJsonObject().get("filePath").getAsString());
//                    file.setSourceName(element.getAsJsonObject().get("fileName").getAsString());
//                    fileList.add(file);
//                }
//            }
//        }
//        return fileList;
//    }
//
//    public static String getFileTokens(String getTokenUrl, String fileName, String filePath, String userCookie) throws Exception{
//        Map<String, String> header = new HashMap<>();
//        header.put("Cookie", userCookie);
//        header.put("Content-type", "application/json");
//
//        String jsonStr = "{\"downloadFileType\": 0,\"expire\": 4000,\"storePath\": \"" + filePath+ "\"}";
//        String oneResult = HttpClientUtil.doPostJsonRequest(getTokenUrl, header, jsonStr);
//        Gson gson = new Gson();
//        JsonObject resultObj = gson.fromJson(oneResult, JsonObject.class);
//        String requestPath = resultObj.getAsJsonObject("result").get("requestPath").getAsString();
//        String token = resultObj.getAsJsonObject("result").get("token").getAsString();
//
//        return requestPath + "?token=" + token + "&fileName=" + fileName + "&storePath=" + filePath;
//    }
//
//
//    public static InputStream downloadFile(String url) throws Exception{
//        CloseableHttpClient client = HttpClients.createDefault();
//        HttpGet httpGet = new HttpGet(url);
//        HttpResponse response = client.execute(httpGet);
//        return response.getEntity().getContent();
//    }
//
////    public static Map<String, String> queryAllFileList(String queryFileUrl, Set<String> dataIdSet, String moduleInfoId, String userCookie) throws Exception {
////        for (String dataId : dataIdSet) {
////            Map<String, String> map = queryFileList(queryFileUrl, dataId, moduleInfoId, userCookie);
////
////        }
////        return null;
////    }
//
//    @Value("${jzfile.query}")
//    String url;
//    @Autowired
//    private ProductFileInformationService service;
//    public static void main(String[] args) throws Exception{
//
//        String url = "https://zjzb.jingdata.com/api/metadata/file/query";
//        String dataId = "5f1bd893eefc914ed7bcd28f";
//        String moduleInfoid = "Object_aumf__u";
//        String userCookie = "saas_login_token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJBUFAiLCJ1c2VyX2lkIjoiNWVhNTJjNmZjMDVlODQxNzgxNWYyMjM1OjVlYTUzMTU5YzA1ZTg0MTc4MTVmYTMyZjoxNzMxMDYyMDMxNCIsImlzcyI6IkppbmdkYXRhIiwiZXhwIjoxNTk2MzY3ODA1LCJpYXQiOjE1OTU1MDM4MDV9.YS3blFOCB37olX_78wETX9TavQMtdq1Pxyp4f4bQC38";
//        String tokenUrl = "https://zjzb.jingdata.com/api/file/preview/downloadPath";
//
//        List<ProductFileInformation> files = JZFileUtil.queryFileList(url, dataId, moduleInfoid, userCookie);
//        for (ProductFileInformation file :files) {
////            log.info(data.getKey() + ":" + data.getValue());
//
//        }
//        InputStream[] ins = new InputStream[files.size()+1];
//        String[] fileNames = new String[files.size()+1];
//        int index = 0;
//        for(ProductFileInformation file :files) {
//            String downloadUrl = JZFileUtil.getFileTokens(tokenUrl, "aa.zip", file.getStorePath(), userCookie);
//            InputStream is = JZFileUtil.downloadFile(downloadUrl);
//            ins[index] = is;
//            fileNames[index] = file.getSourceName();
//            index ++;
//        }
//        for (int i = 0; i < fileNames.length; i++) {
//            fileNames[i] = "aa.txt";
//        }
//        File file = new File("zip/" + UUID.randomUUID().toString() + ".zip");
//        ZipUtil.zip(file, fileNames, ins);
//    }
//
//}
