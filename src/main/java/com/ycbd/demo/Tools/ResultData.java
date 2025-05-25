package com.ycbd.demo.Tools;

import lombok.Data;

/**
 * 通用响应数据封装类
 * @author lxw
 * com.ycbd.common.Interface
 * @create 2022/5/18 14:16
 * @description
 */
@Data
public class ResultData<T> {
    /** 响应状态码 */
    private int code;
    
    /** 响应结果数据 */
    private T result;
    
    /** 响应消息 */
    private String message;
    
    /** 响应时间戳 */
    private long timestamp;

    public ResultData() {
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> ResultData<T> success(T data) {
        ResultData<T> resultData = new ResultData<>();
        resultData.setCode(200);
        resultData.setResult(data);
        return resultData;
    }

    public static <T> ResultData<T> fail(int code, String message) {
        ResultData<T> resultData = new ResultData<>();
        resultData.setCode(code);
        resultData.setMessage(message);
        return resultData;
    }

    public static ResultData<Integer> success() {
        ResultData<Integer> resultData = new ResultData<>();
        resultData.setCode(200);
        return resultData;
    }
}

