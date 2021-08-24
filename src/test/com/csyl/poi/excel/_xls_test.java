package com.csyl.poi.excel;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

public class _xls_test {

    @Test
    @SuppressWarnings({"all"})
    public void paredTest() throws Exception {
        // 获取文件路径和文件
        // 将输入流转换为工作簿对象
        HSSFWorkbook workbook = new HSSFWorkbook(_xls_test.class.
                getClassLoader().
                getResourceAsStream(new String("100Y0065.csv".getBytes(StandardCharsets.UTF_8))));
        // 获取第一个工作表
        HSSFSheet sheet = workbook.getSheet("sheet0");
        // 使用索引获取工作表
        // HSSFSheet sheet = workbook.getSheetAt(0);
        // 获取指定行
        HSSFRow row = sheet.getRow(0);
        // 获取指定列
        HSSFCell cell = row.getCell(2);
        // 打印
        System.out.println(cell.getStringCellValue());
    }
}
