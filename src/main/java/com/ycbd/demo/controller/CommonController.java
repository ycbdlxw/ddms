package com.ycbd.demo.controller;

import com.ycbd.demo.service.CommonService;
import com.ycbd.demo.Tools.ResultData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 通用数据处理控制器
 * 提供通用的CRUD(增删改查)接口,支持任意数据表的操作
 * 通过targetTable参数指定要操作的数据表
 */
@Slf4j
@RestController
@RequestMapping("/api/common")
@RequiredArgsConstructor
public class CommonController {
    
    // 通用服务层接口
    private final CommonService commonService;
    
    /**
     * 通用列表查询接口
     * 支持分页查询和条件过滤
     * 
     * @param pageIndex 页码,从1开始
     * @param pageSize 每页记录数
     * @param params 查询参数,必须包含targetTable字段指定目标表
     * @return 查询结果,包含数据列表和总记录数
     */
    @GetMapping("/list")
    public ResultData<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") int pageIndex,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam Map<String, Object> params) {
        return commonService.getList(pageIndex, pageSize, params);
    }
    
    /**
     * 通用数据保存接口
     * 支持新增和更新操作
     * 当params中包含id字段时执行更新操作,否则执行新增操作
     * 
     * @param params 数据参数,必须包含targetTable字段指定目标表
     * @return 保存结果
     */
    @PostMapping("/save")
    public ResultData<Map<String, Object>> save(@RequestBody Map<String, Object> params) {
        return commonService.saveData(params);
    }
    
    /**
     * 通用数据删除接口
     * 根据id删除指定记录
     * 
     * @param params 参数,必须包含targetTable字段和id字段
     * @return 删除结果
     */
    @PostMapping("/delete")
    public ResultData<Map<String, Object>> delete(@RequestBody Map<String, Object> params) {
        return commonService.deleteData(params);
    }
    
    /**
     * 通用数据详情查询接口
     * 根据id查询单条记录的详细信息
     * 
     * @param params 参数,必须包含targetTable字段和id字段
     * @return 记录详情
     */
    @GetMapping("/detail")
    public ResultData<Map<String, Object>> detail(@RequestParam Map<String, Object> params) {
        return commonService.getDetail(params);
    }
    
    /**
     * 用户登录接口
     * 验证用户名和密码,生成JWT令牌
     * 
     * @param params 登录参数,必须包含username和password字段
     * @return 登录结果,包含用户信息和JWT令牌
     */
    @PostMapping("/login")
    public ResultData<Map<String, Object>> login(@RequestBody Map<String, Object> params) {
        return commonService.login(params);
    }
} 