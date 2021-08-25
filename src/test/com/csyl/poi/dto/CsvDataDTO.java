package com.csyl.poi.dto;

import com.csyl.poi.constant.StringConstant;
import com.csyl.poi.util.DateUtil;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

@Data
public class CsvDataDTO {

    @DataMatch("照片编号")
    private String photoCode;
    @DataMatch("原始照片编号")
    private String orgPhotoCode;
    @DataMatch("文件夹编号")
    private String folderCode;
    @DataMatch("相机编号")
    private String cameraCode;
    @DataMatch("布设点位编号")
    private String pointCode;
    @DataMatch("拍摄日期")
    private String shootingDate;
    @DataMatch("拍摄时间")
    private String shootingTime;
    @DataMatch("工作天数")
    private String workingDays;
    @DataMatch("物种")
    private String species;
    @DataMatch("数量")
    private String quantity;
    @DataMatch("备注")
    private String remark;

    private LocalDateTime shootingLocalDateTime;

    public void _2DateTime() {
        String[] y_m_d = this.shootingDate.split("[/]");
        String y_m_d_str = Arrays.stream(y_m_d)
                .map(this::getString).collect(Collectors.joining("/"));
        String[] h_m_s = this.shootingTime.split("[:]");
        ArrayList<String> arrayList = Arrays.stream(h_m_s)
                .map(this::getString)
                .collect(Collectors.toCollection(ArrayList::new));
        if (arrayList.size() == 2) {
            arrayList.add("00");
        }
        String h_m_s_str = String.join(":", arrayList);
        this.shootingLocalDateTime = DateUtil.str2Date(y_m_d_str + StringConstant.SPACE + h_m_s_str);
    }

    private String getString(String s) {
        if (s.length() == 1) {
            return "0" + s;
        }
        return s;
    }
}
