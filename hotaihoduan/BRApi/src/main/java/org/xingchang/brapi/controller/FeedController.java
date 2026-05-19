package org.xingchang.brapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.xingchang.brapi.common.PageResult;
import org.xingchang.brapi.common.Result;
import org.xingchang.brapi.entity.Content;
import org.xingchang.brapi.service.PersonalizedFeedService;
import org.xingchang.brapi.service.ContentService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Feed流管理")
@RestController
@RequestMapping("/feed")
public class FeedController {
    
    @Autowired
    private PersonalizedFeedService personalizedFeedService;
    
    @Autowired
    private ContentService contentService;
    
    @Operation(summary = "获取个性化Feed流")
    @GetMapping("/personalized")
    public Result<Map<String, Object>> getPersonalizedFeed(
        @RequestParam Long userId,
        @RequestParam(defaultValue = "1") Integer page,
        @RequestParam(defaultValue = "20") Integer size
    ) {
        List<Content> contents = personalizedFeedService.getPersonalizedFeed(userId, page, size);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", contents);
        result.put("total", contents.size());
        result.put("page", page);
        result.put("size", size);
        
        return Result.success(result);
    }
    
    @Operation(summary = "获取配置")
    @GetMapping("/config")
    public Result<Map<String, Object>> getConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("algorithm", "personalized");
        config.put("hotWeight", 40);
        config.put("timeWeight", 30);
        config.put("interactionWeight", 30);
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
        Map<String, Object> stats = contentService.getFeedStats();
        return Result.success(stats);
    }
    
    @Operation(summary = "Feed内容列表")
    @GetMapping("/contentList")
    public Result<PageResult<Content>> getContentList(@RequestParam Map<String, Object> params) {
        PageResult<Content> result = contentService.getFeedContentList(params);
        return Result.success(result);
    }
    
    @Operation(summary = "推送到Feed")
    @PostMapping("/push")
    public Result<Void> pushToFeed(@RequestBody Map<String, Object> params) {
        Long contentId = Long.valueOf(params.get("contentId").toString());
        Double initialScore = 100.0;
        if (params.get("initialScore") != null) {
            initialScore = Double.valueOf(params.get("initialScore").toString());
        }
        boolean success = contentService.pushToFeed(contentId, initialScore);
        return success ? Result.success() : Result.error("推送失败");
    }
    
    @Operation(summary = "从Feed移除")
    @DeleteMapping("/remove/{id}")
    public Result<Void> removeFromFeed(@PathVariable Long id) {
        boolean success = contentService.removeFromFeed(id);
        return success ? Result.success() : Result.error("移除失败");
    }
    
    @Operation(summary = "调整热度分数")
    @PutMapping("/updateHotScore")
    public Result<Void> updateHotScore(@RequestBody Map<String, Object> params) {
        Long contentId = Long.valueOf(params.get("contentId").toString());
        Double hotScore = Double.valueOf(params.get("hotScore").toString());
        boolean success = contentService.updateHotScore(contentId, hotScore);
        return success ? Result.success() : Result.error("更新失败");
    }
}
