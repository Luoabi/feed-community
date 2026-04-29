package org.xingchang.brapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.xingchang.brapi.common.Result;
import org.xingchang.brapi.entity.ContentCategory;
import org.xingchang.brapi.mapper.ContentCategoryMapper;

import java.util.List;
import java.util.Map;

/**
 * 内容分类Controller
 */
@Tag(name = "内容分类")
@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {
    
    private final ContentCategoryMapper categoryMapper;
    
    @Operation(summary = "分类列表")
    @GetMapping("/list")
    public Result<List<ContentCategory>> list() {
        List<ContentCategory> result = categoryMapper.selectList(null);
        return Result.success(result);
    }
    
    @Operation(summary = "分类详情")
    @GetMapping("/detail/{id}")
    public Result<ContentCategory> detail(@PathVariable Long id) {
        ContentCategory category = categoryMapper.selectById(id);
        return Result.success(category);
    }
    
    @Operation(summary = "新增分类")
    @PostMapping("/add")
    public Result<Void> add(@RequestBody ContentCategory category) {
        int rows = categoryMapper.insert(category);
        return rows > 0 ? Result.success() : Result.error();
    }
    
    @Operation(summary = "更新分类")
    @PutMapping("/update")
    public Result<Void> update(@RequestBody ContentCategory category) {
        int rows = categoryMapper.updateById(category);
        return rows > 0 ? Result.success() : Result.error();
    }
    
    @Operation(summary = "删除分类")
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        int rows = categoryMapper.deleteById(id);
        return rows > 0 ? Result.success() : Result.error();
    }
    
    @Operation(summary = "修改状态")
    @PutMapping("/changeStatus")
    public Result<Void> changeStatus(@RequestBody Map<String, Object> params) {
        Long id = Long.valueOf(params.get("id").toString());
        Integer status = (Integer) params.get("status");
        
        ContentCategory category = new ContentCategory();
        category.setId(id);
        category.setStatus(status);
        
        int rows = categoryMapper.updateById(category);
        return rows > 0 ? Result.success() : Result.error();
    }
}
