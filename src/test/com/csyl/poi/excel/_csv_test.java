package com.csyl.poi.excel;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import com.csyl.poi.dto.CsvDataDTO;
import com.csyl.poi.dto.DataMatch;
import com.sun.corba.se.impl.orbutil.ObjectUtility;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

public class _csv_test {

    private final static String GBK = "GBK";

    @Test
    public void read() throws IOException {

        // 第一参数：读取文件的路径 第二个参数：分隔符（不懂仔细查看引用百度百科的那段话） 第三个参数：字符集
        CsvReader csvReader = new CsvReader(_csv_test.class.
                getClassLoader().
                getResource("100Y0065.csv").getPath(), ',', Charset.forName(GBK));
        // 如果你的文件没有表头，这行不用执行
        // 这行不要是为了从表头的下一行读，也就是过滤表头
        csvReader.readHeaders();
        String[] headers = csvReader.getHeaders();
        Arrays.stream(headers).map(a -> a + ":").forEach(System.out::print);
        System.out.println();


        // 读取每行的内容
        List<CsvDataDTO> csvDataDTOList = new ArrayList<>();
        while (csvReader.readRecord()) {
            CsvDataDTO csvDataDTO = new CsvDataDTO();
            Map<String, Field> map = getStringFieldMap(csvDataDTO);

            String collect = Arrays.stream(headers).map(name -> {
                try {
                    String value = csvReader.get(name);
                    if (map.get(name) != null) {
                        map.get(name).setAccessible(true);
                        map.get(name).set(csvDataDTO, value);
                    }
                    return value;
                } catch (IOException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                return "Null";
            }).collect(Collectors.joining(":"));
            csvDataDTOList.add(csvDataDTO);
            System.out.println(collect);
        }

        csvDataDTOList.forEach(CsvDataDTO::_2DateTime);
        csvDataDTOList.forEach(CsvDataDTO::_copyExSpecies);

        List<CsvDataDTO> bufferList = new ArrayList<>();
        for (CsvDataDTO csvDataDTO : csvDataDTOList) {
            if (csvDataDTO.getSpecies() == null || csvDataDTO.getSpecies().equals("")) {
                bufferList.add(csvDataDTO);
            } else {
                if (CollectionUtils.isEmpty(bufferList)) {
                    continue;
                }
                for (CsvDataDTO buffer : bufferList) {
                    buffer.setSpecies(csvDataDTO.getSpecies());
                    buffer = null;
                }
            }
        }

        HashMap<String, List<CsvDataDTO>> collect = csvDataDTOList.stream()
                .collect(Collectors.groupingBy(
                        CsvDataDTO::getSpecies,
                        HashMap::new,
                        Collectors.toList()));


        List<CsvDataSort> resultList = new ArrayList<>();
        for (Map.Entry<String, List<CsvDataDTO>> entry : collect.entrySet()) {
            //物种
            String key = entry.getKey();
            AtomicReference<CsvDataSort> csvDataSort = new AtomicReference<>(new CsvDataSort());
            resultList.add(csvDataSort.get());
            csvDataSort.get().setSpecies(key);
            //集合
            List<CsvDataDTO> csvDataDTOS = entry.getValue();
            csvDataDTOS.stream()
                    .sorted(Comparator.comparing(CsvDataDTO::getShootingLocalDateTime))
                    .forEachOrdered(
                            csvDataDTO -> {
                                if (csvDataSort.get().getLastShootingLocalDateTime() == null) {
                                    firstSet(csvDataSort.get(), csvDataDTO);
                                    return;
                                }
                                LocalDateTime lastShootingLocalDateTime = csvDataSort.get().getLastShootingLocalDateTime();
                                LocalDateTime shootingLocalDateTime = csvDataDTO.getShootingLocalDateTime();

                                Duration duration = java.time.Duration.between(lastShootingLocalDateTime, shootingLocalDateTime);
                                long l = Math.abs(duration.toMinutes());
                                if (l <= 30) {
                                    _set(csvDataSort.get(), csvDataDTO);
                                } else {
                                    csvDataSort.set(new CsvDataSort());
                                    resultList.add(csvDataSort.get());
                                    csvDataSort.get().setSpecies(key);
                                    firstSet(csvDataSort.get(), csvDataDTO);
                                }
                            }
                    );
        }
        csvReader.close();

        //writer
        List<CsvDataDTO> list = new ArrayList<>();
        resultList.stream()
                .sorted(Comparator.comparing(CsvDataSort::getLastShootingLocalDateTime))
                .forEach(csvDataSort -> list.addAll(csvDataSort.getCsvDataDTOS()));
        writer(CsvDataDTO.class, list);
    }

    private void _set(CsvDataSort csvDataSort, CsvDataDTO csvDataDTO) {
        csvDataSort.setLastShootingLocalDateTime(csvDataDTO.getShootingLocalDateTime());
        csvDataSort.getCsvDataDTOS().add(csvDataDTO);
    }

    private void firstSet(CsvDataSort csvDataSort, CsvDataDTO csvDataDTO) {
        csvDataDTO.setMark(1L);
        csvDataSort.setFirstShootingLocalDateTime(csvDataDTO.getShootingLocalDateTime());
        _set(csvDataSort, csvDataDTO);
    }

    private Map<String, Field> getStringFieldMap(CsvDataDTO csvDataDTO) {
        Class<? extends CsvDataDTO> aClass = csvDataDTO.getClass();
        Field[] declaredFields = aClass.getDeclaredFields();
        Map<String, Field> map = new HashMap<>();
        for (Field field : declaredFields) {
            DataMatch annotation = field.getAnnotation(DataMatch.class);
            if (annotation == null) continue;

            String value = annotation.value();
            map.put(value, field);
        }
        return map;
    }


    public static void writer(Class<? extends CsvDataDTO> aClass, List<? extends CsvDataDTO> list) throws IOException {

        Field[] declaredFields = aClass.getDeclaredFields();
        List<String> headers = Arrays.stream(declaredFields).map(field -> {
            DataMatch annotation = field.getAnnotation(DataMatch.class);
            if (Optional.ofNullable(annotation).isPresent()) {
                return annotation.value();
            }
            return "";
        }).collect(Collectors.toList());

        // 第一参数：新生成文件的路径 第二个参数：分隔符（不懂仔细查看引用百度百科的那段话） 第三个参数：字符集
        CsvWriter csvWriter = new CsvWriter("D:/100Y0065_processed.csv", ',', Charset.forName(GBK));

        // 写表头和内容，因为csv文件中区分没有那么明确，所以都使用同一函数，写成功就行
        csvWriter.writeRecord(headers.toArray(new String[0]));

        list.stream().map(csvDataDTO -> {
            Field[] declaredFields1 = csvDataDTO.getClass().getDeclaredFields();
            String[] counts = new String[declaredFields1.length];
            for (int i = 0, declaredFields1Length = declaredFields1.length; i < declaredFields1Length; i++) {
                Field declaredField = declaredFields1[i];
                try {
                    declaredField.setAccessible(true);
                    counts[i] = Optional.ofNullable(declaredField.get(csvDataDTO)).orElse("").toString();
                } catch (IllegalAccessException exception) {
                    exception.printStackTrace();
                }
            }
            return counts;
        }).forEach(
                counts -> {
                    try {
                        csvWriter.writeRecord(counts);
                    } catch (IOException ignored) {
                    }
                }
        );
        // 关闭csvWriter
        csvWriter.close();
    }


    @AllArgsConstructor
    @Setter
    @Getter
    public class CsvDataSort {
        private String species;
        private LocalDateTime firstShootingLocalDateTime;
        private LocalDateTime lastShootingLocalDateTime;
        private final List<CsvDataDTO> csvDataDTOS = new ArrayList<>();
        private volatile Long mark;

        @Deprecated
        protected CsvDataSort() {
        }
    }

}
