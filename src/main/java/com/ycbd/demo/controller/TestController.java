package com.ycbd.demo.controller;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.*;

import com.ycbd.demo.Tools.ResultData;
import com.ycbd.demo.service.AutoTestService;
import com.ycbd.demo.model.TestExecutionResult;

import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
@Slf4j
public class TestController {
    
    private final AutoTestService autoTestService;
    
    /**
     * 执行测试脚本
     * @param scriptPath 脚本路径
     * @param resultDir 结果保存目录(可选),为空时使用默认目录
     * @param useCurrentDir 是否使用脚本所在目录作为基准目录(可选),默认为false
     * @param request HTTP请求对象
     * @return 测试执行结果
     */
    @PostMapping("/run")
    public ResultData<TestExecutionResult> runTests(
            @RequestParam String scriptPath,
            @RequestParam(required = false) String resultDir,
            @RequestParam(required = false, defaultValue = "false") boolean useCurrentDir,
            HttpServletRequest request) {
        try {
            if (StrUtil.isEmpty(scriptPath)) {
                return ResultData.fail(400, "脚本路径不能为空");
            }
            
            TestExecutionResult result = autoTestService.executeTests(scriptPath, resultDir, useCurrentDir);
            
            // 将测试结果存储到request属性中，供拦截器使用
            request.setAttribute("responseBody", result);
            
            return ResultData.success(result);
            
        } catch (Exception e) {
            log.error("测试执行失败", e);
            return ResultData.fail(500, "测试执行失败：" + e.getMessage());
        }
    }
}
