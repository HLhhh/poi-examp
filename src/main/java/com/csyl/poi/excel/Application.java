package com.csyl.poi.excel;

import lombok.SneakyThrows;

import java.io.File;

public class Application {

    @SneakyThrows
    public static void main(String[] args) {

        File file = new File("D:\\test\\csv");
        File[] tempList = file.listFiles();

        for (File itemFile : tempList) {
            Csv csv = new Csv("D:\\test\\csv");
            csv.read(itemFile.getPath());
        }

    }
}
