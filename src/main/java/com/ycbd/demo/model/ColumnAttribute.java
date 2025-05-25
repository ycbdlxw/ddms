package com.ycbd.demo.model;

import lombok.Data;

@Data
public class ColumnAttribute {
    private String dbTableName;
    private String tableName;
    private String name;
    private String pagename;
    private Integer isShowInList;
    private Integer searchFlag;
    private Integer editFlag;
    private String options;
    private String showType;
    private String queryType;
    private String checkMode;
    private Integer isRequired;
    private Integer autoSelectId;
    private Integer orderNo;
    private Integer searchOrderNo;
    private Integer editOrderNo;
    private String defaultValue;
    private Integer len;
    private String fieldType;
    private Integer type;
    private String classcode;
    private String other;
    private Integer isRead;
    private String unionTable;
    private Integer isPri;
    private Integer isForeignKey;
    private String attrType;
    private String attrName;
    private String whereSql;
    private Integer templetResId;
    private String selectColumns;
    private Integer isExport;
    private Integer showWidth;
    private Integer contentLen;
    private String editType;
    private String roles;
    private Integer importRequired;
    private Integer searchWidth;
    private Integer listWidth;
    private Integer isBatch;
    private Integer isMobile;
}
