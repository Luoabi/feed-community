package org.xingchang.brapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.xingchang.brapi.common.PageResult;
import org.xingchang.brapi.common.Result;
import org.xingchang.brapi.entity.Content;
import org.xingchang.brapi.service.PersonalizedFeedService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Feed流管理")
@RestController
@RequestMapping("/feed")
public class FeedController {
    
    @Autowired
    private PersonalizedFeedService personalizedFeedService;
    
    @Operation(summary = "获取个性化Feed流")
    @GetMapping("/personalized")
    public Result<PageResult<Content>> getPersonalizedFeed(
        @RequestParam Long userId,
        @RequestParam(defaultValue = "1") Integer page,
        @RequestParam(defaultValue = "20") Integer size
    ) {
        List<Content> contents = personalizedFeedService.getPersonalizedFeed(userId, page, size);
        return Result.success(PageResult.of((long) contents.size(), contents));
    }
    
    @Operation(summary = "获取配置")
    @GetMapping("/config")
    public Result<Map<String, Object>> getConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("algorithm", "personalized");
        config.put("hotWeight", 0.3);
        config.put("timeWeight", 0.15);
        config.put("interestWeight", 0.5);
        config.put("qualityWeight", 0.05);
        config.put("refreshInterval", 300);
        config.put("cacheTime", 3600);
        return Result.success(config);
    }
    
    @Operation(summary = "更新配置")
    @PutMapping("/updateConfig")
    public Result<Void> updateConfig(@RequestBody Map<String, Object> config) {
        return Result.success();
    }
    
    @Operation(summary = "Feed统计")
    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalContent", 2580);
        stats.put("todayPush", 156);
        stats.put("cacheSize", 1024);
        stats.put("avgScore", 75.5);
        return Result.success(stats);
    }
    
    @Operation(summary = "Feed内容列表")
    @GetMapping("/contentList")
    public Result<PageResult<Object>> getContentList(@RequestParam Map<String, Object> params) {
        return Result.success(PageResult.of(0L, new ArrayList<>()));
    }
    
    @Operation(summary = "推送到Feed")
    @PostMapping("/push")
    public Result<Void> pushToFeed(@RequestBody Map<String, Object> params) {
        return Result.success();
    }
    
    @Operation(summary = "从Feed移除")
    @DeleteMapping("/remove/{id}")
    public Result<Void> removeFromFeed(@PathVariable Long id) {
        return Result.success();
    }
}
