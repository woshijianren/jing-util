package com.exception.util;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;

/**
 * @author zyl
 * @date 2020/6/20 14:50
 */
public class HSSFUtil {

    public static void setCellValue(HSSFRow styleRow, HSSFSheet sheet, int row, int col, String value) {
        //将styleRow的样式复制到目标单元格
        HSSFCell sourceCell = styleRow.getCell(col);
        HSSFRow hssfRow = sheet.getRow(row);
        if (hssfRow == null) {
            hssfRow = sheet.createRow(row);
        }
        HSSFCell cell = hssfRow.getCell(col);
        if (cell == null) {
            cell = hssfRow.createCell(col);
        }
        cell.setCellStyle(sourceCell.getCellStyle());
        cell.setCellValue(value);
    }
}
