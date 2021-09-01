package com.csyl.poi.excel.util;

/**
 * @author 霖
 */
public class FileUtil {

    public static String interceptUrl(String path, String index) {
        return path.substring(0, path.lastIndexOf(index));
    }
}
