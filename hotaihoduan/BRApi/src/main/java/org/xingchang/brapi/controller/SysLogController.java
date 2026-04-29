package org.xingchang.brapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.xingchang.brapi.common.PageResult;
import org.xingchang.brapi.common.Result;
import org.xingchang.brapi.entity.SysLog;
import org.xingchang.brapi.service.SysLogService;

import java.util.Map;

/**
 * 操作日志Controller
 */
@Tag(name = "操作日志管理")
@RestController
@RequestMapping("/log")
@RequiredArgsConstructor
public class SysLogController {
    
    private final SysLogService logService;
    
    @Operation(summary = "日志列表")
    @GetMapping("/list")
    public Result<PageResult<SysLog>> list(@RequestParam Map<String, Object> params) {
        PageResult<SysLog> result = logService.getList(params);
        return Result.success(result);
    }
    
    @Operation(summary = "删除日志")
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        boolean success = logService.removeById(id);
        return success ? Result.success() : Result.error();
    }
    
    @Operation(summary = "清空日志")
    @DeleteMapping("/clear")
    public Result<Void> clear() {
        boolean success = logService.clearAll();
        return success ? Result.success() : Result.error();
    }
}
