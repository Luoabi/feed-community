package org.xingchang.brapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.xingchang.brapi.common.Result;
import org.xingchang.brapi.service.DataInitService;

/**
 * 数据初始化控制器
 * 注意：这些接口仅用于开发和测试，生产环境应该禁用
 */
@Tag(name = "数据初始化")
@RestController
@RequestMapping("/init")
public class DataInitController {
    
    @Autowired
    private DataInitService dataInitService;
    
    @Operation(summary = "初始化内容标签关联")
    @PostMapping("/content-tags")
    public Result<Integer> initContentTags() {
        int count = dataInitService.initContentTags();
        return Result.success(count);
    }
    
    @Operation(summary = "初始化内容热度分数")
    @PostMapping("/hot-scores")
    public Result<Void> initHotScores() {
        dataInitService.initHotScores();
        return Result.success();
    }
    
    @Operation(summary = "完整初始化")
    @PostMapping("/full")
    public Result<Void> fullInit() {
        dataInitService.fullInit();
        return Result.success();
    }
}
