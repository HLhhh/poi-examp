package com.csyl.poi.excel;

import com.csyl.poi.excel.constant.BusinessConstant;
import lombok.SneakyThrows;

import java.io.File;
import java.util.Locale;

/**
 * @author 霖
 */
public class Application {

    @SneakyThrows
    public static void main(String[] commands) {

        String url = commands == null || commands.length == 0 ? "D:\\test\\csv" : commands[0];
        File file = new File(url);
        File[] tempList = file.listFiles();

        Csv csv = new Csv();
        for (File itemFile : tempList) {
            if (itemFile.isFile()) {
                String fileName = itemFile.getName();
                if (BusinessConstant.CSV.equals(fileName.substring(fileName.lastIndexOf(BusinessConstant.DOT)).toLowerCase(Locale.ROOT))) {
                    csv.read(itemFile);
                }
            }
        }

    }
}
