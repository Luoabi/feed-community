package org.xingchang.brapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.xingchang.brapi.common.Result;
import org.xingchang.brapi.entity.SysMenu;
import org.xingchang.brapi.service.SysMenuService;

import java.util.List;

/**
 * 菜单Controller
 */
@Tag(name = "菜单管理")
@RestController
@RequestMapping("/menu")
@RequiredArgsConstructor
public class SysMenuController {
    
    private final SysMenuService menuService;
    
    @Operation(summary = "菜单列表")
    @GetMapping("/list")
    public Result<List<SysMenu>> list() {
        List<SysMenu> result = menuService.getMenuTree();
        return Result.success(result);
    }
    
    @Operation(summary = "新增菜单")
    @PostMapping("/add")
    public Result<Void> add(@RequestBody SysMenu menu) {
        boolean success = menuService.save(menu);
        return success ? Result.success() : Result.error();
    }
    
    @Operation(summary = "更新菜单")
    @PutMapping("/update")
    public Result<Void> update(@RequestBody SysMenu menu) {
        boolean success = menuService.updateById(menu);
        return success ? Result.success() : Result.error();
    }
    
    @Operation(summary = "删除菜单")
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        boolean success = menuService.removeById(id);
        return success ? Result.success() : Result.error();
    }
}
