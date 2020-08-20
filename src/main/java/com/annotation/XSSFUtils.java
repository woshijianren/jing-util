//package com.annotation;
//
//import cn.hutool.core.date.DateUtil;
//import cn.hutool.core.io.FileUtil;
//import cn.hutool.core.util.CharsetUtil;
//import cn.hutool.core.util.IdUtil;
//import cn.hutool.core.util.StrUtil;
//import cn.hutool.core.util.ZipUtil;
//import cn.hutool.poi.excel.cell.CellUtil;
//import cn.hutool.setting.Setting;
//import com.util.FileUtils;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.poi.ss.usermodel.*;
//import org.apache.poi.ss.util.CellRangeAddress;
//import org.apache.poi.xssf.usermodel.*;
//
//import java.io.*;
//import java.lang.reflect.*;
//import java.nio.charset.StandardCharsets;
//import java.util.*;
//
///**
// * @author zyl
// * @date 2020/7/16 17:42
// * @TODO 根据实际需求去修改
// * 所有的List都是经过了@XSSF中的index排序过的
// * 一切都是以getAllField为主，传值传getAllField的结果
// * 批量导出的时候是先导出txt，后导出excel，所以对于文件名txt+1，excel+2
// */
//@Slf4j
//public class XSSFUtils {
//
//    /**
//     * 得到所有的Excel的表头
//     * 即所有被@XSSF注解的字段的@XSSF注解中的header的值
//     * @param fieldList 该字段必须要是被@XSSF注解的，不然会出现异常
//     */
//    private static List<String> getExcelHeader(List<Field> fieldList) {
//        List<String> headerList = new ArrayList<>();
//        for (Field field : fieldList) {
//            XSSF xssf = field.getDeclaredAnnotation(XSSF.class);
//            headerList.add(xssf.header());
//        }
//        return headerList;
//    }
//
//    /**
//     * 得到所有单元格的align样式，不过对方说全部靠右，先留着
//     */
//    private static List<CellStyle> getAllCellStyle(XSSFWorkbook workbook, List<Field> fieldList) {
//        List<CellStyle> cellStyleList = new ArrayList<>();
//        for (Field field : fieldList) {
//            XSSF xssf = field.getDeclaredAnnotation(XSSF.class);
//            HorizontalAlignment align = xssf.align();
//            XSSFCellStyle xssfCellStyle = workbook.createCellStyle();
//            xssfCellStyle.setAlignment(align);
//            cellStyleList.add(xssfCellStyle);
//        }
//        return cellStyleList;
//    }
//
//    // TODO 需要修改
//    public static File getExcelFile(List<?> dataList, String fileName, String numSettingUrl, Boolean isMerge, boolean view) {
//        if (!isBatch && !view) {
//            addDataRecord(fileName, numSettingUrl);
//        }
//        File file = getExcelPathFile(fileName);
//        try (OutputStream os = new FileOutputStream(file)) {
//            getWorkbook(dataList, isMerge).write(os);
//        } catch (IOException e) {
//            log.error(e + "");
//        }
//        return file;
//    }
//
//    /**
//     * 是否需要合并单元格操作
//     */
//    private static XSSFWorkbook getWorkbook(List<?> dataList, Boolean isMerge) {
//        Class<?> clazz = AnalysisXSSF.getListClass(dataList);
//        XSSFWorkbook workbook = new XSSFWorkbook();
//        XSSFSheet sheet = workbook.createSheet();
//        XSSFRow headerRow = sheet.createRow(0);
//        List<Field> fieldList = AnalysisXSSF.getAllFieldWithXSSF(clazz);
//        List<String> headerList = getExcelHeader(fieldList);
//        // 表头样式：居中
//        XSSFCellStyle headerStyle = workbook.createCellStyle();
//        headerStyle.setAlignment(HorizontalAlignment.CENTER);
//        // 写表头
//        for (int i = 0; i < headerList.size(); i++) {
//            XSSFCell cell = headerRow.createCell(i);
//            CellUtil.setCellValue(cell, headerList.get(i), headerStyle);
//        }
//        // 写数据
//        List<Method> methodList = AnalysisXSSF.getAllGetMethodsWithXSSF(dataList);
//        List<CellStyle> cellStyleList = getAllCellStyle(workbook, fieldList);
//        for (int row = 0; row < dataList.size(); row++) {
//            Object data = dataList.get(row);
//            XSSFRow sheetRow = sheet.createRow(row + 1);
//            for (int i = 0; i < methodList.size(); i++) {
//                XSSFCell cell = sheetRow.createCell(i);
//                try {
//                    Object result = methodList.get(i).invoke(data);
//                    // TODO 对数据进行处理
//                    CellUtil.setCellValue(cell, result, cellStyleList.get(i));
//                } catch (IllegalAccessException | InvocationTargetException e) {
//                    log.error(e + "");
//                }
//            }
//        }
//        // 是否需要对同一列的相同值单元格进行合并处理
//        if (isMerge) {
//            mergeCellWithSameValue(workbook, sheet, dataList);
//        }
//        return workbook;
//    }
//
//    /**
//     * 合并同一列相邻单元格之间相同值的单元格
//     */
//    private static void mergeCellWithSameValue(XSSFWorkbook workbook, XSSFSheet sheet, List<?> dataList) {
//        int rowIndex = 0;
//        String mergeValue = "";
//        Map<Integer, Integer> mergeMap = new HashMap<>();
//        for (int row = 0; row < dataList.size(); row++) {
//            String value = sheet.getRow(row).getCell(0).getStringCellValue();
//            if (mergeValue.equals(value)) {
//                mergeMap.put(rowIndex, row);
//            } else {
//                mergeValue = value;
//                rowIndex = row;
//            }
//        }
//        for (Integer key : mergeMap.keySet()) {
//            sheet.addMergedRegion(new CellRangeAddress(key, mergeMap.get(key), 0, 0));
//            XSSFCellStyle cellStyle = workbook.createCellStyle();
//            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
//            cellStyle.setAlignment(HorizontalAlignment.RIGHT);
//            sheet.getRow(key).getCell(0).setCellStyle(cellStyle);
//        }
//    }
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//    // 是否批量下载
//    private static boolean isBatch;
//
//    /**
//     * startDate和endDate是筛选条件的日期，用来拼接txt的每一行的前缀
//     */
//    public static File getTxtZipFile(List<?> dataList, String fileName, String startDate, String endDate, String numSettingUrl) {
//        File txtFile = getTxtFile(dataList, fileName, startDate, endDate, numSettingUrl);
//        return ZipUtil.zip(getZipPathFile(fileName, true), false, txtFile);
//    }
//
//
//    public static File getBatchTxtZip(List<List<?>> dataListList, List<String> fileName, String startDate, String endDate, String numSettingUrl, String tokenUrl, String userCookie) {
//        isBatch = true;
//        List<File> txtFileList = new ArrayList<>();
//        for (int i = 0; i < dataListList.size(); i++) {
//            if (getListClass(dataListList.get(i)).equals(ProductFileInformation.class)) {
//                File txtFile = enclosureFile((List<ProductFileInformation>) dataListList.get(i), tokenUrl, userCookie, fileName.get(i), startDate, endDate, numSettingUrl, true);
//                txtFileList.add(txtFile);
//                continue;
//            }
//            File txtFile = getTxtFile(dataListList.get(i), fileName.get(i), startDate, endDate, numSettingUrl);
//            txtFileList.add(txtFile);
//        }
//        isBatch = false;
//        return ZipUtil.zip(getRandomPathFile(".zip"), false, txtFileList.toArray(new File[0]));
//    }
//
//    public static File getBatchExcelZip(List<List<?>> dataListList, List<String> fileNameList, String numSettingUrl, String tokenUrl, String userCookie, String startDate, String endDate) {
//        isBatch = true;
//        for (String s : fileNameList) {
//            addTwoDataRecord(s, numSettingUrl);
//        }
//        List<File> fileList = new ArrayList<>();
//        for (int i = 0; i < dataListList.size(); i++) {
//            if (getListClass(dataListList.get(i)).equals(ProductFileInformation.class)) {
//                File excelFile = enclosureFile((List<ProductFileInformation>) dataListList.get(i), tokenUrl, userCookie, fileNameList.get(i), startDate, endDate, numSettingUrl, false);
//                fileList.add(excelFile);
//                continue;
//            }
//            if (getListClass(dataListList.get(i)).equals(AssetPortfolio.class)) {
//                fileList.add(getExcelFile(dataListList.get(i), fileNameList.get(i), numSettingUrl, true, false));
//                continue;
//            }
//            File excelFile = getExcelFile(dataListList.get(i), fileNameList.get(i), numSettingUrl, false, false);
//            fileList.add(excelFile);
//        }
//        isBatch = false;
//        return ZipUtil.zip(getRandomPathFile(".zip"), false, fileList.toArray(new File[0]));
//    }
//
//    /**
//     * 文件名拼接①
//     * 通过文件名获取下载次数,然后+1
//     * 例如：CISP-10993001_A1008_V01_1_20200701_13_Z.zip获取13，这个数字要保持至少2为，例如01
//     *
//     * @param fileName
//     * @return
//     */
//    private static String subDataRecord(String fileName) {
//        String dataRecord = fileName.substring(StrUtil.ordinalIndexOf(fileName, "_", 5) + 1, StrUtil.ordinalIndexOf(fileName, "_", 6));
//        Integer times = Integer.parseInt(dataRecord) + 1;
//        if (times < 10) {
//            return "0" + times;
//        }
//        return times + "";
//    }
//
//    private static String addTwoDataRecord(String fileName) {
//        String dataRecord = fileName.substring(StrUtil.ordinalIndexOf(fileName, "_", 5) + 1, StrUtil.ordinalIndexOf(fileName, "_", 6));
//        Integer times = Integer.parseInt(dataRecord) + 2;
//        if (times < 10) {
//            return "0" + times;
//        }
//        return times + "";
//    }
//
//    /**
//     * 文件名拼接②
//     * 这个fileName不包含后面的.TXT之类的后缀
//     *
//     * @param fileName
//     * @return
//     */
//    private static String getFileName(String fileName, boolean isTxt) {
//        String preString = fileName.substring(0, StrUtil.ordinalIndexOf(fileName, "_", 5) + 1);
//        String sufString = fileName.substring(fileName.lastIndexOf("_"));
//        if (isBatch) {
//            if (!isTxt) {
//                return preString + addTwoDataRecord(fileName) + sufString;
//            }
//        }
//
//        return preString + subDataRecord(fileName) + sufString;
//    }
//
//    /**
//     * 文件名拼接③
//     *
//     * @param fileName
//     * @return
//     */
//    private static File getExcelPathFile(String fileName) {
//        return new File(FileUtils.SYS_TEM_DIR + getFileName(fileName.substring(0, fileName.lastIndexOf(".")), false) + ".xlsx");
//    }
//
//    private static File getTxtPathFile(String fileName) {
//        return new File(FileUtils.SYS_TEM_DIR + getFileName(fileName.substring(0, fileName.lastIndexOf(".")), true) + ".txt");
//    }
//
//    private static File getZipPathFile(String fileName, boolean isTxt) {
//        return new File(FileUtils.SYS_TEM_DIR + getFileName(fileName.substring(0, fileName.lastIndexOf(".")), isTxt) + ".zip");
//    }
//
//    public static File getTxtFile(List<?> dataList, String fileName, String startDate, String endDate, String numSettingUrl) {
//        if (!isBatch) {
//            addDataRecord(fileName, numSettingUrl);
//        }
//        // 增加一个处理金额不需要三位分节法的问题
//        List<Integer> indexList = new ArrayList<>();
//        List<Field> fieldList = getAllField(getListClass(dataList));
//        for (int i = 0; i < fieldList.size(); i++) {
//            XSSF xssf = fieldList.get(i).getAnnotation(XSSF.class);
//            if (xssf.isMoney()) {
//                indexList.add(i);
//            }
//        }
//
//        File file = getTxtPathFile(fileName);
//        try (OutputStream os = new FileOutputStream(file)) {
//            for (Object data : dataList) {
//                os.write(getTxtPrefix(fileName, startDate, endDate).getBytes(StandardCharsets.UTF_8));
//                List<Method> methodList = getAllMethod(dataList);
//                for (int i = 0; i < methodList.size(); i++) {
//                    Object result = methodList.get(i).invoke(data);
//                    if (result == null) {
//                        if (fieldList.get(i).getAnnotation(XSSF.class).isMoney()) {
//                            os.write("0.00|".getBytes(StandardCharsets.UTF_8));
//                            continue;
//                        } else if (fieldList.get(i).getAnnotation(XSSF.class).isNumber()) {
//                            os.write("0|".getBytes(StandardCharsets.UTF_8));
//                            continue;
//                        } else {
//                            os.write("|".getBytes(StandardCharsets.UTF_8));
//                            continue;
//                        }
//                    }
//                    if (indexList.contains(i)) {
//                        os.write((StrUtil.replace(result.toString(), ",", "") + "|").getBytes(StandardCharsets.UTF_8));
//                        continue;
//                    }
//                    os.write((result.toString() + "|").getBytes(StandardCharsets.UTF_8));
//                }
//                os.write(System.getProperty("line.separator").getBytes(StandardCharsets.UTF_8));
//            }
//            os.flush();
//        } catch (IOException | IllegalAccessException | InvocationTargetException e) {
//            log.error(e + "");
//        }
//        return file;
//    }
//
//    /**
//     * 截取文件日期
//     */
//    private static String subFileDate(String fileName) {
//        return fileName.substring(StrUtil.ordinalIndexOf(fileName, "_", 4) + 1, StrUtil.ordinalIndexOf(fileName, "_", 5));
//    }
//
//    /**
//     * 截取文件接口名称
//     */
//    private static String subCode(String fileName) {
//        return fileName.substring(StrUtil.ordinalIndexOf(fileName, "_", 1) + 1, StrUtil.ordinalIndexOf(fileName, "_", 2));
//    }
//
//    /**
//     * 增加一次下载次数
//     *
//     * @param fileName
//     */
//    private static void addDataRecord(String fileName, String numSettingUrl) {
//        String dateStr = subFileDate(fileName);
//        String fileDate = dateStr.substring(0, 4) + "/" + dateStr.substring(4, 6) + "/" + dateStr.substring(6, 8);
//        Setting numSetting = new Setting(FileUtil.touch(numSettingUrl), CharsetUtil.CHARSET_UTF_8, false);
//        numSetting.set(subCode(fileName) + "_" + fileDate, subDataRecord(fileName));
//        numSetting.store(numSettingUrl);
//    }
//
//    /**
//     * 增加一次下载次数
//     *
//     * @param fileName
//     */
//    private static void addTwoDataRecord(String fileName, String numSettingUrl) {
//        String dateStr = subFileDate(fileName);
//        String fileDate = dateStr.substring(0, 4) + "/" + dateStr.substring(4, 6) + "/" + dateStr.substring(6, 8);
//        Setting numSetting = new Setting(FileUtil.touch(numSettingUrl), CharsetUtil.CHARSET_UTF_8, false);
//        numSetting.set(subCode(fileName) + "_" + fileDate, addTwoDataRecord(fileName));
//        numSetting.store(numSettingUrl);
//    }
//
////    /**
////     * 得到某个类中的所有被@XSSF所注释的字段
////     *
////     * @param clazz
////     * @return
////     */
////    public static List<Field> getAllField(Class clazz) {
////        Map<Integer, Field> fieldMap = new TreeMap<>(new Comparator<Integer>() {
////            @Override
////            public int compare(Integer o1, Integer o2) {
////                return o1 - o2;
////            }
////        });
////        Field[] declaredFields = clazz.getDeclaredFields();
////        for (Field declaredField : declaredFields) {
////            if (declaredField.isAnnotationPresent(XSSF.class)) {
////                XSSF xssf = declaredField.getDeclaredAnnotation(XSSF.class);
////                fieldMap.put(xssf.index(), declaredField);
////            }
////        }
////        return new ArrayList<>(fieldMap.values());
////    }
//
//
////    /**
////     * 得到所有字段的值
////     *
////     * @param fieldList
////     * @param methodList
////     * @param object
////     * @return
////     */
////    private static List<Object> getAllValue(List<Field> fieldList, List<Method> methodList, Object object) {
////        List<Object> valueList = new ArrayList<>();
////        for (Method method : methodList) {
////            try {
////                valueList.add(method.invoke(object));
////            } catch (IllegalAccessException | InvocationTargetException e) {
////                log.error(e + "");
////            }
////        }
////        return valueList;
////    }
//
////    /**
////     * 得到所有get方法
////     *
////     * @param fieldList
////     * @param clazz
////     * @return
////     */
////    private static List<Method> getAllGetMethod(List<Field> fieldList, Class clazz) {
////        List<Method> methodList = new ArrayList<>();
////        for (Field field : fieldList) {
////            methodList.add(splicePrefixMethod("get", field, clazz));
////        }
////        return methodList;
////    }
////
////    public static List<Method> getAllSetMethods(Class clazz) {
////        List<Field> fieldList = getAllField(clazz);
////        List<Method> methodList = new ArrayList<>();
////        for (Field field : fieldList) {
////            String fieldName = field.getName();
////            try {
////                methodList.add(clazz.getDeclaredMethod("set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1), String.class));
////            } catch (NoSuchMethodException e) {
////                log.error(e + "");
////            }
////        }
////        return methodList;
////    }
//
////    /**
////     * 拼接方法，一般都是前缀加字段名首字母大写
////     *
////     * @param prefix
////     * @param field
////     * @param clazz
////     * @return
////     */
////    private static Method splicePrefixMethod(String prefix, Field field, Class clazz) {
////        String fieldName = field.getName();
////        prefix += fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
////        try {
////            return clazz.getDeclaredMethod(prefix);
////        } catch (NoSuchMethodException e) {
////            log.error(e + "");
////        }
////        return null;
////    }
//
//
////    private static void setUpExcelRow(Row row, List<CellStyle> cellStyleList, List<?> valueList, List<Method> methodList) {
////        for (int i = 0; i < cellStyleList.size(); i++) {
////            try {
////                CellUtil.setCellValue(CellUtil.getOrCreateCell(row, i), methodList.get(i).invoke(valueList.get(i)), cellStyleList.get(i));
////            } catch (IllegalAccessException | InvocationTargetException e) {
////                log.error(e + "");
////            }
////        }
////    }
////
////    private static void setUpExcelSheet(XSSFWorkbook workbook, List<?> dataList) {
////        Class<?> clazz = dataList.get(0).getClass();
////        List<Field> fieldList = getAllField(clazz);
////        List<CellStyle> cellStyleList = getAllCellStyle(workbook, fieldList);
////        List<Method> methodList = getAllGetMethod(fieldList, clazz);
////        Sheet sheet = workbook.createSheet();
////        for (int i = 0; i < dataList.size(); i++) {
////            Row row = sheet.createRow(i);
////            setUpExcelRow(row, cellStyleList, dataList, methodList);
////        }
////    }
//
//
//    private static String spliceTxtPrefix(Object... obj) {
//        StringBuilder sb = new StringBuilder();
//        for (Object o : obj) {
//            sb.append(o.toString()).append("|");
//        }
//        return sb.toString();
//    }
//
//    private static String getTxtPrefix(String fileName, String startDate, String endDate) {
//        int dateTypeIndex = StrUtil.ordinalIndexOf(fileName, "_", 3);
//        int fileNameIndex = StrUtil.ordinalIndexOf(fileName, "_", 4);
//        return spliceTxtPrefix(Constant.txtPrefix, DateUtil.parseDate(endDate).year(), fileName.substring(dateTypeIndex + 1, fileNameIndex), startDate, endDate);
//    }
//
//    private static String getRandomPath(String suffix) {
//        if (!suffix.startsWith(".")) {
//            suffix = "." + suffix;
//        }
//        return FileUtils.SYS_TEM_DIR + IdUtil.fastSimpleUUID() + suffix;
//    }
//
//    private static File getRandomPathFile(String suffix) {
//        return new File(getRandomPath(suffix));
//    }
//
//
//    /**
//     * @param isTxt 是否是txt，不是就下载excel
//     */
//    public static File enclosureFile(List<ProductFileInformation> dataList, String tokenUrl, String userCookie, String fileName, String startTime, String endTime, String numSettingUrl, Boolean isTxt) {
//        //准备好TXT或者是Excel文件，存入dataList用于gzip打包
//        InputStream[] ins = new InputStream[dataList.size() + 1];
//        String[] fileNames = new String[dataList.size() + 1];
//
//        try {
//            int index = 0;
//            Map<String, Integer> repeatName = new HashMap<>();
//            for (ProductFileInformation file : dataList) {
//                // service.spliceCookie(userToken)
//                String downloadUrl = JZFileUtil.getFileTokens(tokenUrl, file.getFileName(), file.getStorePath(), userCookie);
//                file.setDownloadUrl(downloadUrl);
//
//                //下载JZ的文件拿到inputstream
//                ins[index] = JZFileUtil.downloadFile(downloadUrl);
//                String name = file.getFileName();
//                if (repeatName.containsKey(name)) {
//                    repeatName.put(name, repeatName.get(name) + 1);
//                } else {
//                    repeatName.put(name, 0);
//                }
//                if (repeatName.get(name) != 0) {
//                    fileNames[index] = file.getFileName().substring(0, file.getFileName().lastIndexOf(".")) + "(" + repeatName.get(name) + ")" + file.getFileName().substring(file.getFileName().lastIndexOf("."));
//                } else {
//                    fileNames[index] = file.getFileName();
//                }
//
//                index++;
//            }
//            File originFile;
//            //把TXT或者Excel的fileName和inputStream添加到数组中
//            if (isTxt) {
//                originFile = XSSFUtils.getTxtFile(dataList, fileName, startTime, endTime, numSettingUrl);
//            } else {
//                originFile = XSSFUtils.getExcelFile(dataList, fileName, numSettingUrl, false, false);
//            }
//            ins[index] = new FileInputStream(originFile);
//            fileNames[index] = originFile.getName();
//
//            return ZipUtil.zip(getZipPathFile(fileName, isTxt), fileNames, ins);
//
//
//        } catch (Exception e) {
//            log.error(e + "");
//
//        } finally {
//            for (InputStream inputStream : ins) {
//                try {
//                    inputStream.close();
//                } catch (IOException e) {
//                    log.error("关闭InputStream异常，" + e.getMessage());
//                }
//            }
//        }
//        return null;
//    }
//
//
////    /**
////     * 根据传入的dataList解析为其具体类型并获取该类中所有的方法
////     * @param dataList List<?>
////     * @return 方法集合
////     */
////    private static List<Method> getAllMethod(List<?> dataList) {
////        Class<?> clazz = getListClass(dataList);
////        List<Field> fieldList = getAllField(clazz);
////        return getAllGetMethod(fieldList, clazz);
////    }
////
////    /**
////     * 必须传入一个内部类写法的List
////     * @param dataList 必须是内部类写法才能被解析，不然会出现转换异常
////     * @return 集合的具体类型
////     */
////    private static Class getListClass(List<?> dataList) {
////        Type type = dataList.getClass().getGenericSuperclass();
////        return (Class) ((ParameterizedType) type).getActualTypeArguments()[0];
////    }
//}
