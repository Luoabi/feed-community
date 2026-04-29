package org.xingchang.brapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.xingchang.brapi.common.Result;
import org.xingchang.brapi.service.TagService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 标签Controller
 */
@Tag(name = "标签管理")
@RestController
@RequestMapping("/tag")
@RequiredArgsConstructor
public class TagController {
    
    private final TagService tagService;
    
    @Operation(summary = "标签列表")
    @GetMapping("/list")
    public Result<List<org.xingchang.brapi.entity.Tag>> list() {
        List<org.xingchang.brapi.entity.Tag> result = tagService.getList(null, null);
        return Result.success(result);
    }
    
    @Operation(summary = "标签详情")
    @GetMapping("/detail/{id}")
    public Result<org.xingchang.brapi.entity.Tag> detail(@PathVariable Long id) {
        org.xingchang.brapi.entity.Tag tag = tagService.getById(id);
        return Result.success(tag);
    }
    
    @Operation(summary = "新增标签")
    @PostMapping("/add")
    public Result<Void> add(@RequestBody org.xingchang.brapi.entity.Tag tag) {
        boolean success = tagService.save(tag);
        return success ? Result.success() : Result.error();
    }
    
    @Operation(summary = "更新标签")
    @PutMapping("/update")
    public Result<Void> update(@RequestBody org.xingchang.brapi.entity.Tag tag) {
        boolean success = tagService.updateById(tag);
        return success ? Result.success() : Result.error();
    }
    
    @Operation(summary = "删除标签")
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        boolean success = tagService.removeById(id);
        return success ? Result.success() : Result.error();
    }
    
    @Operation(summary = "用户标签列表")
    @GetMapping("/userList")
    public Result<List<Map<String, Object>>> getUserTagList(@RequestParam Map<String, Object> params) {
        // 简化版，实际应该查询用户标签关联表
        return Result.success(List.of());
    }
    
    @Operation(summary = "分配标签")
    @PostMapping("/assign")
    public Result<Void> assignTag(@RequestBody Map<String, Object> params) {
        // 简化版，实际应该插入用户标签关联表
        return Result.success();
    }
    
    @Operation(summary = "移除标签")
    @DeleteMapping("/remove")
    public Result<Void> removeTag(@RequestBody Map<String, Object> params) {
        // 简化版，实际应该删除用户标签关联表记录
        return Result.success();
    }
    
    @Operation(summary = "批量分配")
    @PostMapping("/batchAssign")
    public Result<Void> batchAssign(@RequestBody Map<String, Object> params) {
        // 简化版，实际应该批量插入用户标签关联表
        return Result.success();
    }
    
    @Operation(summary = "标签统计")
    @GetMapping("/stats")
    public Result<Map<String, Object>> getTagStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalTags", tagService.count());
        stats.put("activeTags", tagService.getList(null, 1).size());
        return Result.success(stats);
    }
    
    @Operation(summary = "标签推荐")
    @GetMapping("/recommend/{userId}")
    public Result<List<org.xingchang.brapi.entity.Tag>> getTagRecommend(@PathVariable Long userId) {
        // 简化版，实际应该根据用户行为推荐标签
        List<org.xingchang.brapi.entity.Tag> tags = tagService.getList(null, 1);
        return Result.success(tags.subList(0, Math.min(5, tags.size())));
    }
    
    @Operation(summary = "用户兴趣分析")
    @GetMapping("/userInterest/{userId}")
    public Result<Map<String, Object>> getUserInterest(@PathVariable Long userId) {
        // 简化版，实际应该分析用户兴趣
        Map<String, Object> interest = new HashMap<>();
        interest.put("userId", userId);
        interest.put("tags", List.of());
        return Result.success(interest);
    }
}
