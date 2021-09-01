package com.csyl.poi.excel.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author 霖
 */
public class DateUtil {
    private final static DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    public static LocalDateTime str2Date(String str) {
        return LocalDateTime.parse(str, df);
    }

}
