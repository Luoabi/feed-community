package org.xingchang.brapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.xingchang.brapi.common.PageResult;
import org.xingchang.brapi.common.Result;
import org.xingchang.brapi.entity.UserCollect;
import org.xingchang.brapi.entity.UserLike;
import org.xingchang.brapi.entity.UserShare;
import org.xingchang.brapi.service.InteractionService;

import java.util.ArrayList;
import java.util.Map;

@Tag(name = "互动数据")
@RestController
@RequestMapping("/interaction")
public class InteractionController {
    
    @Autowired
    private InteractionService interactionService;
    
    @Operation(summary = "点赞列表")
    @GetMapping("/like/list")
    public Result<PageResult<UserLike>> getLikeList(@RequestParam Map<String, Object> params) {
        return Result.success(interactionService.getLikeList(params));
    }
    
    @Operation(summary = "收藏列表")
    @GetMapping("/collect/list")
    public Result<PageResult<UserCollect>> getCollectList(@RequestParam Map<String, Object> params) {
        return Result.success(interactionService.getCollectList(params));
    }
    
    @Operation(summary = "分享列表")
    @GetMapping("/share/list")
    public Result<PageResult<UserShare>> getShareList(@RequestParam Map<String, Object> params) {
        return Result.success(interactionService.getShareList(params));
    }
    
    @Operation(summary = "互动统计")
    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        return Result.success(interactionService.getStats());
    }
    
    @Operation(summary = "用户行为分析")
    @GetMapping("/userBehavior")
    public Result<Object> getUserBehavior() {
        return Result.success(new ArrayList<>());
    }
    
    @Operation(summary = "内容互动排行")
    @GetMapping("/contentRank")
    public Result<Object> getContentRank() {
        return Result.success(new ArrayList<>());
    }
    
    @Operation(summary = "我的点赞")
    @GetMapping("/my-likes")
    public Result<PageResult<Map<String, Object>>> getMyLikes(@RequestParam Map<String, Object> params,
                                                               jakarta.servlet.http.HttpServletRequest request) {
        // ✅ 从JWT Token中获取当前用户ID
        Long userId = (Long) request.getAttribute("userId");
        params.put("userId", userId);
        return Result.success(interactionService.getMyLikes(params));
    }
    
    @Operation(summary = "我的收藏")
    @GetMapping("/my-collects")
    public Result<PageResult<Map<String, Object>>> getMyCollects(@RequestParam Map<String, Object> params,
                                                                  jakarta.servlet.http.HttpServletRequest request) {
        // ✅ 从JWT Token中获取当前用户ID
        Long userId = (Long) request.getAttribute("userId");
        params.put("userId", userId);
        return Result.success(interactionService.getMyCollects(params));
    }
    
    @Operation(summary = "取消收藏")
    @DeleteMapping("/collect/{id}")
    public Result<Void> cancelCollect(@PathVariable Long id) {
        boolean success = interactionService.cancelCollect(id);
        return success ? Result.success() : Result.error();
    }
    
    @Operation(summary = "点赞内容")
    @PostMapping("/like")
    public Result<Map<String, Object>> likeContent(@RequestBody Map<String, Object> params,
                                                    jakarta.servlet.http.HttpServletRequest request) {
        // ✅ 从JWT Token中获取当前用户信息
        Long userId = (Long) request.getAttribute("userId");
        String username = (String) request.getAttribute("username");
        
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        
        Long contentId = Long.valueOf(params.get("contentId").toString());
        
        try {
            boolean isLiked = interactionService.likeContent(userId, username, contentId);
            
            Map<String, Object> result = Map.of(
                "isLiked", isLiked,
                "message", isLiked ? "点赞成功" : "取消点赞"
            );
            
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @Operation(summary = "收藏内容")
    @PostMapping("/collect")
    public Result<Map<String, Object>> collectContent(@RequestBody Map<String, Object> params,
                                                       jakarta.servlet.http.HttpServletRequest request) {
        // ✅ 从JWT Token中获取当前用户信息
        Long userId = (Long) request.getAttribute("userId");
        String username = (String) request.getAttribute("username");
        
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        
        Long contentId = Long.valueOf(params.get("contentId").toString());
        
        try {
            boolean isCollected = interactionService.collectContent(userId, username, contentId);
            
            Map<String, Object> result = Map.of(
                "isCollected", isCollected,
                "message", isCollected ? "收藏成功" : "取消收藏"
            );
            
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @Operation(summary = "检查点赞和收藏状态")
    @GetMapping("/check-status")
    public Result<Map<String, Object>> checkStatus(@RequestParam Long contentId,
                                                    jakarta.servlet.http.HttpServletRequest request) {
        // ✅ 从JWT Token中获取当前用户ID（可能为空，未登录）
        Long userId = (Long) request.getAttribute("userId");
        
        boolean isLiked = interactionService.checkUserLiked(userId, contentId);
        boolean isCollected = interactionService.checkUserCollected(userId, contentId);
        
        Map<String, Object> result = Map.of(
            "isLiked", isLiked,
            "isCollected", isCollected
        );
        
        return Result.success(result);
    }
}
