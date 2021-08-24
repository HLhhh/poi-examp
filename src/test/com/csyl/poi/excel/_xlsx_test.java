package com.csyl.poi.excel;

import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class _xlsx_test {

    @Test
    @SuppressWarnings({"all"})
    public void paredTest() throws Exception {
        // 将输入流转换为工作簿对象
        XSSFWorkbook workbook = new XSSFWorkbook(_xlsx_test.class.
                getClassLoader().
                getResourceAsStream(new String("100Y0065.csv".getBytes(StandardCharsets.UTF_8))));
        // 获取第一个工作表
        int activeSheetIndex = workbook.getActiveSheetIndex();
        XSSFSheet sheet = workbook.getSheetAt(activeSheetIndex);
        List<CellRangeAddress> mergedRegions = sheet.getMergedRegions();

        int physicalNumberOfRows = sheet.getPhysicalNumberOfRows();
        for (int i = 0; i < physicalNumberOfRows; i++) {
            // 获取指定行
            XSSFRow row = sheet.getRow(i);
            int physicalNumberOfCells = row.getPhysicalNumberOfCells();
            for (int j = 0; j < physicalNumberOfCells; j++) {
                // 获取指定列
                XSSFCell cell = row.getCell(j);
                AtomicReference<CellRangeAddress> cellAddresses = new AtomicReference<>();
                mergedRegions.stream().forEachOrdered(rangAddress -> {
                    if (rangAddress.isInRange(cell)) {
                        cellAddresses.set(rangAddress);
                        return;
                    }
                });
                // 打印
                if (cellAddresses.get() != null) {
                    int firstColumn = cellAddresses.get().getFirstColumn();
                    int firstRow = cellAddresses.get().getFirstRow();
                    System.out.println(sheet.getRow(firstRow).getCell(firstColumn).getStringCellValue());
                } else {
                    System.out.println(cell.getStringCellValue());
                }
            }
        }
    }

}
