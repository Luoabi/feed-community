package org.xingchang.brapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.xingchang.brapi.common.PageResult;
import org.xingchang.brapi.common.Result;
import org.xingchang.brapi.entity.SysRole;
import org.xingchang.brapi.service.SysRoleService;

import java.util.Map;

@Tag(name = "角色管理")
@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
public class SysRoleController {
    
    private final SysRoleService roleService;
    
    @Operation(summary = "角色列表")
    @GetMapping("/list")
    public Result<PageResult<SysRole>> list(@RequestParam Map<String, Object> params) {
        return Result.success(roleService.getList(params));
    }
    
    @Operation(summary = "角色详情")
    @GetMapping("/detail/{id}")
    public Result<SysRole> detail(@PathVariable Long id) {
        return Result.success(roleService.getById(id));
    }
    
    @Operation(summary = "新增角色")
    @PostMapping("/add")
    public Result<Void> add(@RequestBody SysRole role) {
        return roleService.save(role) ? Result.success() : Result.error();
    }
    
    @Operation(summary = "更新角色")
    @PutMapping("/update")
    public Result<Void> update(@RequestBody SysRole role) {
        return roleService.updateById(role) ? Result.success() : Result.error();
    }
    
    @Operation(summary = "删除角色")
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        return roleService.removeById(id) ? Result.success() : Result.error();
    }
    
    @Operation(summary = "分配权限")
    @PostMapping("/assignPermission")
    public Result<Void> assignPermission(@RequestBody Map<String, Object> params) {
        // 实际应该更新角色的menuIds
        return Result.success();
    }
    
    @Operation(summary = "修改状态")
    @PutMapping("/changeStatus")
    public Result<Void> changeStatus(@RequestBody Map<String, Object> params) {
        Long id = Long.valueOf(params.get("id").toString());
        Integer status = (Integer) params.get("status");
        return roleService.updateStatus(id, status) ? Result.success() : Result.error();
    }
}
