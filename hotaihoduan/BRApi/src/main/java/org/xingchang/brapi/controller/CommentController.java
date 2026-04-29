package org.xingchang.brapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.xingchang.brapi.common.PageResult;
import org.xingchang.brapi.common.Result;
import org.xingchang.brapi.entity.Comment;
import org.xingchang.brapi.service.CommentService;
import org.xingchang.brapi.util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 评论Controller
 */
@Tag(name = "评论管理")
@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {
    
    private final CommentService commentService;
    private final JwtUtil jwtUtil;
    
    @Operation(summary = "评论列表（管理后台）")
    @GetMapping("/list")
    public Result<PageResult<Comment>> list(@RequestParam Map<String, Object> params) {
        PageResult<Comment> result = commentService.getList(params);
        return Result.success(result);
    }
    
    @Operation(summary = "评论树（小程序端：一级评论+部分二级回复）")
    @GetMapping("/tree")
    public Result<PageResult<Map<String, Object>>> tree(@RequestParam Map<String, Object> params) {
        PageResult<Map<String, Object>> result = commentService.getCommentTree(params);
        return Result.success(result);
    }
    
    @Operation(summary = "获取某条评论的所有回复")
    @GetMapping("/replies/{parentId}")
    public Result<PageResult<Comment>> replies(
            @PathVariable Long parentId,
            @RequestParam Map<String, Object> params) {
        PageResult<Comment> result = commentService.getReplies(parentId, params);
        return Result.success(result);
    }
    
    @Operation(summary = "评论详情")
    @GetMapping("/detail/{id}")
    public Result<Comment> detail(@PathVariable Long id) {
        Comment comment = commentService.getById(id);
        return Result.success(comment);
    }
    
    @Operation(summary = "添加评论")
    @PostMapping("/add")
    public Result<Void> add(@RequestBody Comment comment, HttpServletRequest request) {
        // 从 JWT Token 中获取用户信息
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            try {
                Long userId = jwtUtil.getUserIdFromToken(token);
                String username = jwtUtil.getUsernameFromToken(token);
                
                comment.setUserId(userId);
                comment.setUserName(username);
                // TODO: 从用户表获取头像
                // comment.setUserAvatar(userAvatar);
            } catch (Exception e) {
                return Result.error("用户未登录");
            }
        } else {
            return Result.error("用户未登录");
        }
        
        boolean success = commentService.addComment(comment);
        return success ? Result.success() : Result.error();
    }
    
    @Operation(summary = "删除评论")
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        boolean success = commentService.deleteComment(id);
        return success ? Result.success() : Result.error();
    }
    
    @Operation(summary = "批量删除")
    @DeleteMapping("/batchDelete")
    public Result<Void> batchDelete(@RequestBody Map<String, Object> params) {
        @SuppressWarnings("unchecked")
        List<Long> ids = (List<Long>) params.get("ids");
        for (Long id : ids) {
            commentService.deleteComment(id);
        }
        return Result.success();
    }
    
    @Operation(summary = "批量通过审核")
    @PutMapping("/batchApprove")
    public Result<Void> batchApprove(@RequestBody Map<String, Object> params) {
        @SuppressWarnings("unchecked")
        List<Long> ids = (List<Long>) params.get("ids");
        // 批量更新状态为1（通过）
        for (Long id : ids) {
            commentService.updateStatus(id, 1);
        }
        return Result.success();
    }
    
    @Operation(summary = "点赞评论")
    @PostMapping("/like")
    public Result<Map<String, Object>> like(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Long commentId = Long.valueOf(params.get("commentId").toString());
        
        // 从 JWT Token 中获取用户信息
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            try {
                Long userId = jwtUtil.getUserIdFromToken(token);
                String username = jwtUtil.getUsernameFromToken(token);
                
                boolean isLiked = commentService.likeComment(userId, username, commentId);
                
                Map<String, Object> result = new HashMap<>();
                result.put("isLiked", isLiked);
                result.put("message", isLiked ? "点赞成功" : "取消点赞");
                
                return Result.success(result);
            } catch (Exception e) {
                return Result.error("用户未登录");
            }
        } else {
            return Result.error("用户未登录");
        }
    }
    
    @Operation(summary = "检查评论点赞状态")
    @GetMapping("/check-like-status")
    public Result<Map<String, Object>> checkLikeStatus(
            @RequestParam Long commentId,
            HttpServletRequest request) {
        
        Map<String, Object> result = new HashMap<>();
        result.put("isLiked", false);
        
        // 从 JWT Token 中获取用户信息
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            try {
                Long userId = jwtUtil.getUserIdFromToken(token);
                boolean isLiked = commentService.checkUserLiked(userId, commentId);
                result.put("isLiked", isLiked);
            } catch (Exception e) {
                // 用户未登录，返回默认值
            }
        }
        
        return Result.success(result);
    }
    
    @Operation(summary = "置顶/取消置顶")
    @PutMapping("/toggleTop")
    public Result<Void> toggleTop(@RequestBody Map<String, Object> params) {
        Long id = Long.valueOf(params.get("id").toString());
        Integer isTop = params.get("isTop") instanceof Integer 
            ? (Integer) params.get("isTop") 
            : Integer.valueOf(params.get("isTop").toString());
        boolean success = commentService.toggleTop(id, isTop);
        return success ? Result.success() : Result.error();
    }
    
    @Operation(summary = "修改状态（审核）")
    @PutMapping("/changeStatus")
    public Result<Void> changeStatus(@RequestBody Map<String, Object> params) {
        Long id = Long.valueOf(params.get("id").toString());
        Integer status = params.get("status") instanceof Integer 
            ? (Integer) params.get("status") 
            : Integer.valueOf(params.get("status").toString());
        boolean success = commentService.updateStatus(id, status);
        return success ? Result.success() : Result.error();
    }
}
