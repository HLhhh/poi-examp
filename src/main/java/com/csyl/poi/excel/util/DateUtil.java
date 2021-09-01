package com.csyl.poi.excel.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtil {
    private final static DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    private final static ZoneId zoneId = ZoneId.systemDefault();

    public static LocalDateTime str2Date(String str) {
        LocalDateTime dateTime = LocalDateTime.parse(str, df);
        System.out.println(dateTime.format(df)); // not using toString
        return dateTime;
    }


    public static Date localDateTime2Date(LocalDateTime localDateTime) {
        ZonedDateTime zdt = localDateTime.atZone(zoneId);
        return Date.from(zdt.toInstant());
    }

    public static void main(String[] args) {
        System.out.println(localDateTime2Date(str2Date("2020/11/24 14:54:00")));
    }
}
