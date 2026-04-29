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

/**
 * 帖子Controller（已合并到Content，使用content_type='post'区分）
 */
@Tag(name = "帖子管理")
@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {
    
    private final ContentService contentService;
    
    @Operation(summary = "帖子列表")
    @GetMapping("/list")
    public Result<PageResult<Content>> list(@RequestParam Map<String, Object> params) {
        // 只查询帖子类型的内容
        params.put("contentSource", "user");  // 用户发布的内容
        PageResult<Content> result = contentService.getList(params);
        return Result.success(result);
    }
    
    @Operation(summary = "我的帖子")
    @GetMapping("/my")
    public Result<PageResult<Content>> myPosts(@RequestParam Map<String, Object> params,
                                            jakarta.servlet.http.HttpServletRequest request) {
        // ✅ 从JWT Token中获取当前用户ID
        Long userId = (Long) request.getAttribute("userId");
        params.put("authorId", userId);
        params.put("contentSource", "user");  // 用户发布的内容
        PageResult<Content> result = contentService.getList(params);
        return Result.success(result);
    }
    
    @Operation(summary = "帖子详情")
    @GetMapping("/detail/{id}")
    public Result<Content> detail(@PathVariable Long id) {
        Content content = contentService.getById(id);
        return Result.success(content);
    }
    
    @Operation(summary = "新增帖子")
    @PostMapping("/add")
    public Result<Void> add(@RequestBody Content content) {
        // 设置为用户发布的帖子
        content.setContentSource("user");
        if (content.getStatus() == null) {
            content.setStatus("published");  // 默认已发布
        }
        boolean success = contentService.save(content);
        return success ? Result.success() : Result.error();
    }
    
    @Operation(summary = "更新帖子")
    @PutMapping("/update")
    public Result<Void> update(@RequestBody Content content) {
        boolean success = contentService.updateById(content);
        return success ? Result.success() : Result.error();
    }
    
    @Operation(summary = "删除帖子")
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        boolean success = contentService.removeById(id);
        return success ? Result.success() : Result.error();
    }
    
    @Operation(summary = "批量删除")
    @DeleteMapping("/batchDelete")
    public Result<Void> batchDelete(@RequestBody Map<String, Object> params) {
        @SuppressWarnings("unchecked")
        List<Long> ids = (List<Long>) params.get("ids");
        boolean success = contentService.removeByIds(ids);
        return success ? Result.success() : Result.error();
    }
    
    @Operation(summary = "置顶/取消置顶")
    @PutMapping("/toggleTop")
    public Result<Void> toggleTop(@RequestBody Map<String, Object> params) {
        Long id = Long.valueOf(params.get("id").toString());
        Integer isTop = params.get("isTop") instanceof Integer 
            ? (Integer) params.get("isTop") 
            : Integer.valueOf(params.get("isTop").toString());
        boolean success = contentService.toggleTop(id, isTop);
        return success ? Result.success() : Result.error();
    }
    
    @Operation(summary = "精华/取消精华")
    @PutMapping("/toggleEssence")
    public Result<Void> toggleEssence(@RequestBody Map<String, Object> params) {
        Long id = Long.valueOf(params.get("id").toString());
        Integer isEssence = params.get("isEssence") instanceof Integer 
            ? (Integer) params.get("isEssence") 
            : Integer.valueOf(params.get("isEssence").toString());
        boolean success = contentService.toggleEssence(id, isEssence);
        return success ? Result.success() : Result.error();
    }
}
