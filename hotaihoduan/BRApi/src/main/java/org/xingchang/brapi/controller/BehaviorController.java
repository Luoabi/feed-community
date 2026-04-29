package org.xingchang.brapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.xingchang.brapi.common.Result;
import org.xingchang.brapi.service.UserBehaviorService;

import java.util.Map;

/**
 * 用户行为追踪控制器
 */
@Tag(name = "用户行为追踪")
@RestController
@RequestMapping("/behavior")
public class BehaviorController {
    
    @Autowired
    private UserBehaviorService behaviorService;
    
    @Operation(summary = "记录用户行为")
    @PostMapping("/track")
    public Result<Void> trackBehavior(@RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        Long contentId = Long.valueOf(params.get("contentId").toString());
        String actionType = params.get("actionType").toString();
        Integer duration = params.containsKey("duration") ? 
            Integer.valueOf(params.get("duration").toString()) : 0;
        String source = params.containsKey("source") ? 
            params.get("source").toString() : null;
        String ip = params.containsKey("ip") ? 
            params.get("ip").toString() : null;
        String device = params.containsKey("device") ? 
            params.get("device").toString() : null;
        
        boolean success = behaviorService.trackBehavior(
            userId, contentId, actionType, duration, source, ip, device
        );
        
        return success ? Result.success() : Result.error("记录行为失败");
    }
    
    @Operation(summary = "记录浏览行为")
    @PostMapping("/view")
    public Result<Void> trackView(@RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        Long contentId = Long.valueOf(params.get("contentId").toString());
        Integer duration = params.containsKey("duration") ? 
            Integer.valueOf(params.get("duration").toString()) : 0;
        String source = params.containsKey("source") ? 
            params.get("source").toString() : "feed";
        
        boolean success = behaviorService.trackView(userId, contentId, duration, source);
        return success ? Result.success() : Result.error("记录浏览失败");
    }
    
    @Operation(summary = "记录点击行为")
    @PostMapping("/click")
    public Result<Void> trackClick(@RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        Long contentId = Long.valueOf(params.get("contentId").toString());
        String source = params.containsKey("source") ? 
            params.get("source").toString() : "feed";
        
        boolean success = behaviorService.trackClick(userId, contentId, source);
        return success ? Result.success() : Result.error("记录点击失败");
    }
}
