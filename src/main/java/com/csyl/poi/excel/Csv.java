package com.csyl.poi.excel;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import com.csyl.poi.excel.constant.BusinessConstant;
import com.csyl.poi.excel.dto.CsvDataDTO;
import com.csyl.poi.excel.dto.DataMatch;
import com.csyl.poi.excel.util.FileUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Csv {

    /**
     * @param itemFile
     * @throws IOException
     */
    public void read(File itemFile) throws Exception {

        CsvReader csvReader = new CsvReader(itemFile.getPath(), BusinessConstant.COMMA, Charset.forName(FileUtil.codeString(itemFile)));
        csvReader.readHeaders();
        String[] headers = csvReader.getHeaders();

        List<CsvDataDTO> csvDataDTOList = this.getCsvDataDTOS(csvReader, headers);
        csvReader.close();

        List<CsvDataDTO> list = new ArrayList<>();
        this.performClassification(csvDataDTOList).forEach(csvDataSort -> list.addAll(csvDataSort.getCsvDataDTOS()));
        Map<String, CsvDataDTO> hasMarkMap = list.stream()
                .filter(csvDataDTO -> csvDataDTO.getMark() != null)
                .collect(Collectors.toMap(CsvDataDTO::getPhotoCode, Function.identity()));
        csvDataDTOList.forEach(csvDataDTO -> {
            csvDataDTO.setShootingLocalDateTime(null);
            if (Optional.ofNullable(hasMarkMap.get(csvDataDTO.getPhotoCode())).isPresent()) {
                csvDataDTO.setMark(1L);
            }
        });

        writer(CsvDataDTO.class, csvDataDTOList, itemFile);
    }

    private List<CsvDataSort> performClassification(List<CsvDataDTO> csvDataDTOList) {
        Map<String, List<CsvDataDTO>> speciesGroupList = csvDataDTOList.stream()
                .filter(csvDataDTO -> csvDataDTO.getSpecies() != null && !"".equals(csvDataDTO.getSpecies()))
                .collect(Collectors.groupingBy(CsvDataDTO::getSpecies));

        List<CsvDataSort> resultList = new ArrayList<>();
        for (Map.Entry<String, List<CsvDataDTO>> entry : speciesGroupList.entrySet()) {
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

                                Duration duration = Duration.between(lastShootingLocalDateTime, shootingLocalDateTime);
                                long l = Math.abs(duration.toMinutes());
                                if (l <= BusinessConstant.INTERVALS) {
                                    set(csvDataSort.get(), csvDataDTO);
                                } else {
                                    csvDataSort.set(new CsvDataSort());
                                    resultList.add(csvDataSort.get());
                                    csvDataSort.get().setSpecies(key);
                                    firstSet(csvDataSort.get(), csvDataDTO);
                                }
                            }
                    );
        }
        return resultList;
    }

    private List<CsvDataDTO> getCsvDataDTOS(CsvReader csvReader, String[] headers) throws IOException {
        List<CsvDataDTO> resultList = new ArrayList<>();
        while (csvReader.readRecord()) {
            CsvDataDTO csvDataDTO = new CsvDataDTO();
            Map<String, Field> map = this.extractHeader(csvDataDTO.getClass());

            Arrays.stream(headers).forEach(name -> {
                try {
                    String value = csvReader.get(name);
                    if (map.get(name) != null) {
                        map.get(name).setAccessible(true);
                        map.get(name).set(csvDataDTO, value);
                    }
                } catch (Exception ignored) {
                    ignored.printStackTrace();
                }
            });
            resultList.add(csvDataDTO);
        }
        resultList.forEach(CsvDataDTO::addShootingLocalDateTime);
        return resultList;
    }

    private void set(CsvDataSort csvDataSort, CsvDataDTO csvDataDTO) {
        csvDataSort.setLastShootingLocalDateTime(csvDataDTO.getShootingLocalDateTime());
        csvDataSort.getCsvDataDTOS().add(csvDataDTO);
    }

    private void firstSet(CsvDataSort csvDataSort, CsvDataDTO csvDataDTO) {
        csvDataDTO.setMark(1L);
        csvDataSort.setFirstShootingLocalDateTime(csvDataDTO.getShootingLocalDateTime());
        set(csvDataSort, csvDataDTO);
    }

    private Map<String, Field> extractHeader(Class<? extends CsvDataDTO> aClass) {
        Field[] declaredFields = aClass.getDeclaredFields();
        LinkedHashMap<String, Field> map = new LinkedHashMap<>();
        for (Field field : declaredFields) {
            DataMatch annotation = field.getAnnotation(DataMatch.class);
            if (annotation == null) {
                continue;
            }

            String value = annotation.value();
            map.put(value, field);
        }
        return map;
    }


    public void writer(Class<? extends CsvDataDTO> aClass, List<? extends CsvDataDTO> list, File itemFile) throws IOException {

        String processUrl = FileUtil.interceptUrl(itemFile.getPath(), BusinessConstant.SLASH2) + BusinessConstant.SLASH2 + BusinessConstant.PROCESS;

        File fileDir = new File(processUrl);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        CsvWriter csvWriter = new CsvWriter(fileDir.getPath() + BusinessConstant.SLASH + FileUtil.interceptUrl(itemFile.getName(), BusinessConstant.DOT) + BusinessConstant.PROCESS + BusinessConstant.CSV,
                BusinessConstant.COMMA,
                Charset.forName(BusinessConstant.GBK));

        Set<String> headers = extractHeader(aClass).keySet();
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
    @NoArgsConstructor
    @Setter
    @Getter
    public class CsvDataSort {
        private String species;
        private LocalDateTime firstShootingLocalDateTime;
        private LocalDateTime lastShootingLocalDateTime;
        private final List<CsvDataDTO> csvDataDTOS = new ArrayList<>();
        private volatile Long mark;
    }

}
