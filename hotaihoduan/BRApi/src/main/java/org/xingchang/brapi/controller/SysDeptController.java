package org.xingchang.brapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.xingchang.brapi.common.Result;
import org.xingchang.brapi.entity.SysDept;
import org.xingchang.brapi.service.SysDeptService;

import java.util.List;

/**
 * 部门Controller
 */
@Tag(name = "部门管理")
@RestController
@RequestMapping("/dept")
@RequiredArgsConstructor
public class SysDeptController {
    
    private final SysDeptService deptService;
    
    @Operation(summary = "部门列表")
    @GetMapping("/list")
    public Result<List<SysDept>> list() {
        List<SysDept> result = deptService.getDeptTree();
        return Result.success(result);
    }
    
    @Operation(summary = "新增部门")
    @PostMapping("/add")
    public Result<Void> add(@RequestBody SysDept dept) {
        boolean success = deptService.save(dept);
        return success ? Result.success() : Result.error();
    }
    
    @Operation(summary = "更新部门")
    @PutMapping("/update")
    public Result<Void> update(@RequestBody SysDept dept) {
        boolean success = deptService.updateById(dept);
        return success ? Result.success() : Result.error();
    }
    
    @Operation(summary = "删除部门")
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        boolean success = deptService.removeById(id);
        return success ? Result.success() : Result.error();
    }
}
