package org.xingchang.brapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.xingchang.brapi.common.PageResult;
import org.xingchang.brapi.common.Result;
import org.xingchang.brapi.entity.Content;
import org.xingchang.brapi.service.ContentService;

import java.util.List;
import java.util.Map;

@Tag(name = "内容管理")
@RestController
@RequestMapping("/content")
@RequiredArgsConstructor
public class ContentController {
    
    private final ContentService contentService;
    
    @Operation(summary = "内容列表")
    @GetMapping("/list")
    public Result<PageResult<Content>> list(@RequestParam Map<String, Object> params) {
        return Result.success(contentService.getList(params));
    }

    
    @Operation(summary = "内容详情")
    @GetMapping("/detail/{id}")
    public Result<Content> detail(@PathVariable Long id) {
        Content content = contentService.getContentById(id);  // ✅ 使用新方法，会手动处理 images
        if (content != null) {
            // ✅ 增加浏览数
            contentService.incrementViews(id);
        }
        return Result.success(content);
    }
    
    @Operation(summary = "新增内容")
    @PostMapping("/add")
    public Result<Void> add(@RequestBody Content content, jakarta.servlet.http.HttpServletRequest request) {
        try {
            // ✅ 设置默认值
            if (content.getViews() == null) content.setViews(0);
            if (content.getLikes() == null) content.setLikes(0);
            if (content.getComments() == null) content.setComments(0);
            if (content.getShares() == null) content.setShares(0);
            if (content.getCollects() == null) content.setCollects(0);
            if (content.getIsTop() == null) content.setIsTop(0);
            if (content.getIsHot() == null) content.setIsHot(0);
            if (content.getIsEssence() == null) content.setIsEssence(0);
            if (content.getIsRecommend() == null) content.setIsRecommend(0);
            if (content.getDeleted() == null) content.setDeleted(0);
            
            // ✅ 如果是用户发布的内容，需要验证登录
            if ("user".equals(content.getContentSource())) {
                // 从 JWT Token 中获取当前用户信息
                Long userId = (Long) request.getAttribute("userId");
                String username = (String) request.getAttribute("username");
                
                // 用户发布必须登录
                if (userId == null) {
                    return Result.error(401, "请先登录");
                }
                
                // 自动设置作者信息
                content.setAuthorId(userId);
                if (username != null && content.getAuthorName() == null) {
                    content.setAuthorName(username);
                }
                
                // 用户发布的内容默认为待审核状态
                if (content.getStatus() == null) {
                    content.setStatus("pending");
                }
            } else {
                // 管理员发布的内容
                if (content.getContentSource() == null) {
                    content.setContentSource("admin");
                }
                if (content.getStatus() == null) {
                    content.setStatus("published");
                }
                
                // ✅ 管理员发布时，从 JWT Token 获取作者信息（如果没有提供）
                if (content.getAuthorId() == null) {
                    Long userId = (Long) request.getAttribute("userId");
                    String username = (String) request.getAttribute("username");
                    if (userId != null) {
                        content.setAuthorId(userId);
                        if (username != null && content.getAuthorName() == null) {
                            content.setAuthorName(username);
                        }
                    }
                }
            }
            
            // ✅ 设置发布时间
            if ("published".equals(content.getStatus()) && content.getPublishTime() == null) {
                content.setPublishTime(java.time.LocalDateTime.now());
            }
            
            boolean success = contentService.save(content);
            return success ? Result.success() : Result.error("保存失败");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("新增内容失败：" + e.getMessage());
        }
    }
    
    @Operation(summary = "更新内容")
    @PutMapping("/update")
    public Result<Void> update(@RequestBody Content content) {
        return contentService.updateById(content) ? Result.success() : Result.error();
    }
    
    @Operation(summary = "删除内容")
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        return contentService.removeById(id) ? Result.success() : Result.error();
    }
    
    @Operation(summary = "批量删除")
    @DeleteMapping("/batchDelete")
    public Result<Void> batchDelete(@RequestBody Map<String, List<Long>> params) {
        List<Long> ids = params.get("ids");
        return contentService.batchDelete(ids) ? Result.success() : Result.error();
    }
    
    @Operation(summary = "置顶/取消置顶")
    @PutMapping("/toggleTop")
    public Result<Void> toggleTop(@RequestBody Map<String, Object> params) {
        Long id = Long.valueOf(params.get("id").toString());
        Object isTopObj = params.get("isTop");
        Integer isTop = convertToInteger(isTopObj);
        return contentService.toggleTop(id, isTop) ? Result.success() : Result.error();
    }
    
    @Operation(summary = "推荐/取消推荐")
    @PutMapping("/toggleRecommend")
    public Result<Void> toggleRecommend(@RequestBody Map<String, Object> params) {
        Long id = Long.valueOf(params.get("id").toString());
        Object isRecommendObj = params.get("isRecommend");
        Integer isRecommend = convertToInteger(isRecommendObj);
        return contentService.toggleRecommend(id, isRecommend) ? Result.success() : Result.error();
    }
    
    private Integer convertToInteger(Object value) {
        if (value == null) return 0;
        if (value instanceof Integer) return (Integer) value;
        if (value instanceof Boolean) return (Boolean) value ? 1 : 0;
        if (value instanceof String) {
            String str = (String) value;
            if ("true".equalsIgnoreCase(str) || "1".equals(str)) return 1;
            return 0;
        }
        try {
            return Integer.valueOf(value.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    @Operation(summary = "通过审核")
    @PutMapping("/approve")
    public Result<Void> approve(@RequestBody Map<String, Object> params) {
        Long id = Long.valueOf(params.get("id").toString());
        return contentService.approveContent(id) ? Result.success() : Result.error();
    }
    
    @Operation(summary = "拒绝审核")
    @PutMapping("/reject")
    public Result<Void> reject(@RequestBody Map<String, Object> params) {
        Long id = Long.valueOf(params.get("id").toString());
        String reason = (String) params.get("reason");
        String violationType = (String) params.get("violationType");
        return contentService.rejectContent(id, reason, violationType) ? Result.success() : Result.error();
    }
    
    @Operation(summary = "批量通过审核")
    @PutMapping("/batchApprove")
    public Result<Void> batchApprove(@RequestBody Map<String, Object> params) {
        @SuppressWarnings("unchecked")
        List<Long> ids = (List<Long>) params.get("ids");
        return contentService.batchApprove(ids) ? Result.success() : Result.error();
    }
    
    @Operation(summary = "批量拒绝审核")
    @PutMapping("/batchReject")
    public Result<Void> batchReject(@RequestBody Map<String, Object> params) {
        @SuppressWarnings("unchecked")
        List<Long> ids = (List<Long>) params.get("ids");
        String reason = (String) params.get("reason");
        String violationType = (String) params.get("violationType");
        return contentService.batchReject(ids, reason, violationType) ? Result.success() : Result.error();
    }
    
    @Operation(summary = "审核统计")
    @GetMapping("/auditStats")
    public Result<Map<String, Object>> auditStats() {
        return Result.success(contentService.getAuditStats());
    }
}
