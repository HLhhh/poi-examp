package com.csyl.poi.excel.dto;

import com.csyl.poi.excel.constant.BusinessConstant;
import com.csyl.poi.excel.constant.StringConstant;
import com.csyl.poi.excel.util.DateUtil;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import static com.csyl.poi.excel.constant.BusinessConstant.COLON;
import static com.csyl.poi.excel.constant.BusinessConstant.DONT_HAS_SECOND;
import static com.csyl.poi.excel.constant.BusinessConstant.SLASH;
import static com.csyl.poi.excel.constant.BusinessConstant.ZERO_STR;

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
    @DataMatch("独立拍摄")
    private Long mark;
    private LocalDateTime shootingLocalDateTime;

    public void addShootingLocalDateTime() {
        ArrayList<String> HMSList = Arrays.stream(this.shootingTime.split(COLON))
                .map(this::additionalZero)
                .collect(Collectors.toCollection(ArrayList::new));
        if (HMSList.size() == DONT_HAS_SECOND) {
            HMSList.add(BusinessConstant.DOUlE_ZERO_STR);
        }
        this.shootingLocalDateTime = DateUtil.str2Date(Arrays.stream(this.shootingDate.split(SLASH))
                .map(this::additionalZero)
                .collect(Collectors.joining(SLASH)) + StringConstant.SPACE + String.join(COLON, HMSList));
    }

    private String additionalZero(String dateStr) {
        return dateStr.length() == 1 ? ZERO_STR + dateStr : dateStr;
    }
}
