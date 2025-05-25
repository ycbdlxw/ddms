package com.ycbd.demo.Tools;

import java.util.Map;

import cn.hutool.core.util.StrUtil;

public class Tools {
    /**
     * 将Map中的Key值转换成SQL中的列名
     * @param map
     * @return
     */
    public static String getMapKeysToColumns(Map<String, Object> map){
        String columns="";
        for (Object key : map.keySet()) {
            if(columns.contains(key.toString()))continue;
            columns += key + ",";
        }
        columns=StrUtil.removeSuffix(columns,",");
       return columns;
    }
}
