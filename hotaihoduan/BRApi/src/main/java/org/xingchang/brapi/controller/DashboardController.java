package org.xingchang.brapi.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xingchang.brapi.common.Result;
import org.xingchang.brapi.entity.Content;
import org.xingchang.brapi.entity.SysUser;
import org.xingchang.brapi.entity.Comment;
import org.xingchang.brapi.service.ContentService;
import org.xingchang.brapi.service.SysUserService;
import org.xingchang.brapi.service.CommentService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "仪表盘")
@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    
    private final ContentService contentService;
    private final SysUserService userService;
    private final CommentService commentService;
    
    @Operation(summary = "统计数据")
    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 统计用户总数
        long userCount = userService.count();
        stats.put("userCount", userCount);
        
        // 统计内容总数
        long contentCount = contentService.count(new LambdaQueryWrapper<Content>()
            .eq(Content::getDeleted, 0));
        stats.put("contentCount", contentCount);
        
        // 统计评论总数
        long commentCount = commentService.count();
        stats.put("commentCount", commentCount);
        
        // 统计今日访问（这里用今日内容数代替）
        java.time.LocalDateTime todayStart = java.time.LocalDateTime.of(
            java.time.LocalDate.now(), 
            java.time.LocalTime.MIN
        );
        long todayContentCount = contentService.count(new LambdaQueryWrapper<Content>()
            .ge(Content::getCreateTime, todayStart)
            .eq(Content::getDeleted, 0));
        stats.put("todayUserCount", todayContentCount);
        
        return Result.success(stats);
    }
    
    @Operation(summary = "最近内容")
    @GetMapping("/recentContent")
    public Result<List<Map<String, Object>>> getRecentContent() {
        // 查询最近3条已发布的内容
        List<Content> contentList = contentService.list(new LambdaQueryWrapper<Content>()
            .eq(Content::getStatus, "published")
            .eq(Content::getDeleted, 0)
            .orderByDesc(Content::getCreateTime)
            .last("LIMIT 3"));
        
        // 转换为前端需要的格式
        List<Map<String, Object>> result = contentList.stream().map(content -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", content.getId());
            map.put("title", content.getTitle());
            map.put("author", content.getAuthorName() != null ? content.getAuthorName() : "匿名");
            map.put("status", "已发布");
            map.put("createTime", content.getCreateTime());
            map.put("publishTime", content.getPublishTime());
            return map;
        }).toList();
        
        return Result.success(result);
    }
    
    @Operation(summary = "待审核内容")
    @GetMapping("/pendingAudit")
    public Result<List<Map<String, Object>>> getPendingAudit() {
        // 查询最近3条待审核的内容
        List<Content> contentList = contentService.list(new LambdaQueryWrapper<Content>()
            .eq(Content::getStatus, "pending")
            .eq(Content::getDeleted, 0)
            .orderByDesc(Content::getCreateTime)
            .last("LIMIT 3"));
        
        // 转换为前端需要的格式
        List<Map<String, Object>> result = contentList.stream().map(content -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", content.getId());
            map.put("title", content.getTitle());
            map.put("author", content.getAuthorName() != null ? content.getAuthorName() : "匿名");
            map.put("createTime", content.getCreateTime());
            return map;
        }).toList();
        
        return Result.success(result);
    }
    
    @Operation(summary = "热门内容")
    @GetMapping("/hotContent")
    public Result<Object> getHotContent() {
        return Result.success(new java.util.ArrayList<>());
    }
    
    @Operation(summary = "用户增长趋势")
    @GetMapping("/userGrowthTrend")
    public Result<Object> getUserGrowthTrend() {
        return Result.success(new java.util.ArrayList<>());
    }
    
    @Operation(summary = "内容发布趋势")
    @GetMapping("/contentPublishTrend")
    public Result<Object> getContentPublishTrend() {
        return Result.success(new java.util.ArrayList<>());
    }
}
