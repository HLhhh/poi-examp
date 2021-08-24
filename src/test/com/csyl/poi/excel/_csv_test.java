package com.csyl.poi.excel;

import com.csvreader.CsvReader;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.stream.Collectors;

public class _csv_test {

    @Test
    public void read() throws IOException {

        // 第一参数：读取文件的路径 第二个参数：分隔符（不懂仔细查看引用百度百科的那段话） 第三个参数：字符集
        CsvReader csvReader = new CsvReader(_csv_test.class.
                getClassLoader().
                getResource("100Y0065.csv").getPath(), ',', Charset.forName("GBK"));
        // 如果你的文件没有表头，这行不用执行
        // 这行不要是为了从表头的下一行读，也就是过滤表头
        csvReader.readHeaders();
        String[] headers = csvReader.getHeaders();
        Arrays.stream(headers).map(a -> a + ":").forEach(System.out::print);
        System.out.println();

        // 读取每行的内容
        while (csvReader.readRecord()) {
            String collect = Arrays.stream(headers).map(a -> {
                try {
                    return csvReader.get(a);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return "Null";
            }).collect(Collectors.joining(":"));
            System.out.println(collect);
        }

        csvReader.close();
    }

}
