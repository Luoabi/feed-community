package org.xingchang.brapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.xingchang.brapi.common.PageResult;
import org.xingchang.brapi.common.Result;
import org.xingchang.brapi.entity.Content;
import org.xingchang.brapi.service.ContentService;
import org.xingchang.brapi.service.InteractionService;

import java.util.Map;

/**
 * 用户中心Controller
 * 处理"我的发布"、"我的点赞"、"我的收藏"等功能
 */
@Tag(name = "用户中心")
@RestController
@RequestMapping("/user-center")
@RequiredArgsConstructor
public class UserCenterController {
    
    private final ContentService contentService;
    private final InteractionService interactionService;
    
    /**
     * 我的发布
     * 查询当前用户发布的所有内容
     */
    @Operation(summary = "我的发布")
    @GetMapping("/my-posts")
    public Result<PageResult<Content>> getMyPosts(
            @RequestParam Map<String, Object> params,
            HttpServletRequest request) {
        
        // ✅ 从 JWT Token 中获取当前用户ID
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error("未登录");
        }
        
        // 设置查询条件
        params.put("authorId", userId);
        
        PageResult<Content> result = contentService.getMyPosts(params);
        
        // 隐藏敏感信息
        result.getList().forEach(content -> {
            // 可以根据需要隐藏某些字段
        });
        
        return Result.success(result);
    }
    
    /**
     * 我的点赞
     * 查询当前用户点赞的所有内容
     */
    @Operation(summary = "我的点赞")
    @GetMapping("/my-likes")
    public Result<PageResult<Map<String, Object>>> getMyLikes(
            @RequestParam Map<String, Object> params,
            HttpServletRequest request) {
        
        // ✅ 从 JWT Token 中获取当前用户ID
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error("未登录");
        }
        
        // 设置查询条件
        params.put("userId", userId);
        
        PageResult<Map<String, Object>> result = interactionService.getMyLikes(params);
        return Result.success(result);
    }
    
    /**
     * 我的收藏
     * 查询当前用户收藏的所有内容
     */
    @Operation(summary = "我的收藏")
    @GetMapping("/my-collects")
    public Result<PageResult<Map<String, Object>>> getMyCollects(
            @RequestParam Map<String, Object> params,
            HttpServletRequest request) {
        
        // ✅ 从 JWT Token 中获取当前用户ID
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error("未登录");
        }
        
        // 设置查询条件
        params.put("userId", userId);
        
        PageResult<Map<String, Object>> result = interactionService.getMyCollects(params);
        return Result.success(result);
    }
    
    /**
     * 删除我的发布
     */
    @Operation(summary = "删除我的发布")
    @DeleteMapping("/my-posts/{id}")
    public Result<Void> deleteMyPost(
            @PathVariable Long id,
            HttpServletRequest request) {
        
        // ✅ 从 JWT Token 中获取当前用户ID
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error("未登录");
        }
        
        // 查询内容
        Content content = contentService.getById(id);
        if (content == null) {
            return Result.error("内容不存在");
        }
        
        // 验证是否是本人发布的
        if (!content.getAuthorId().equals(userId)) {
            return Result.error("无权删除他人的内容");
        }
        
        // ✅ 级联删除：删除内容及相关数据
        boolean success = contentService.deleteContentWithRelations(id);
        return success ? Result.success() : Result.error("删除失败");
    }
    
    /**
     * 取消收藏
     */
    @Operation(summary = "取消收藏")
    @DeleteMapping("/my-collects/{id}")
    public Result<Void> cancelCollect(@PathVariable Long id) {
        boolean success = interactionService.cancelCollect(id);
        return success ? Result.success() : Result.error("取消收藏失败");
    }
    
    /**
     * 我的统计数据
     * 返回用户的发布数、点赞数、收藏数等统计信息
     */
    @Operation(summary = "我的统计数据")
    @GetMapping("/my-stats")
    public Result<Map<String, Object>> getMyStats(HttpServletRequest request) {
        // ✅ 从 JWT Token 中获取当前用户ID
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error("未登录");
        }
        
        // 统计我的发布数
        Map<String, Object> postParams = Map.of("authorId", userId, "pageNum", 1, "pageSize", 1);
        PageResult<Content> posts = contentService.getMyPosts(postParams);
        
        // 统计我的点赞数
        Map<String, Object> likeParams = Map.of("userId", userId, "pageNum", 1, "pageSize", 1);
        PageResult<Map<String, Object>> likes = interactionService.getMyLikes(likeParams);
        
        // 统计我的收藏数
        Map<String, Object> collectParams = Map.of("userId", userId, "pageNum", 1, "pageSize", 1);
        PageResult<Map<String, Object>> collects = interactionService.getMyCollects(collectParams);
        
        Map<String, Object> stats = Map.of(
            "postsCount", posts.getTotal(),      // 发布数
            "likesCount", likes.getTotal(),      // 点赞数
            "collectsCount", collects.getTotal() // 收藏数
        );
        
        return Result.success(stats);
    }
}
