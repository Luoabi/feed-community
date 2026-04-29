package org.xingchang.brapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.xingchang.brapi.common.Result;
import org.xingchang.brapi.entity.UserInterestProfile;
import org.xingchang.brapi.service.UserInterestService;

import java.util.List;
import java.util.Map;

/**
 * 用户兴趣画像控制器
 */
@Tag(name = "用户兴趣画像")
@RestController
@RequestMapping("/interest")
public class UserInterestController {
    
    @Autowired
    private UserInterestService interestService;
    
    @Operation(summary = "获取用户兴趣标签")
    @GetMapping("/user/{userId}")
    public Result<List<UserInterestProfile>> getUserInterests(@PathVariable Long userId) {
        List<UserInterestProfile> interests = interestService.getUserInterests(userId);
        return Result.success(interests);
    }
    
    @Operation(summary = "获取用户Top N兴趣标签")
    @GetMapping("/user/{userId}/top")
    public Result<List<UserInterestProfile>> getTopInterests(
        @PathVariable Long userId,
        @RequestParam(defaultValue = "5") Integer topN
    ) {
        List<UserInterestProfile> interests = interestService.getTopInterests(userId, topN);
        return Result.success(interests);
    }
    
    @Operation(summary = "设置用户兴趣（冷启动）")
    @PostMapping("/user/{userId}/set")
    public Result<Void> setUserInterests(
        @PathVariable Long userId,
        @RequestBody Map<String, Object> params
    ) {
        @SuppressWarnings("unchecked")
        List<Long> tagIds = (List<Long>) params.get("tagIds");
        boolean success = interestService.setUserInterests(userId, tagIds);
        return success ? Result.success() : Result.error("设置兴趣失败");
    }
    
    @Operation(summary = "手动更新用户兴趣画像")
    @PostMapping("/user/{userId}/update")
    public Result<Void> updateUserInterest(@PathVariable Long userId) {
        interestService.updateUserInterest(userId);
        return Result.success();
    }
    
    @Operation(summary = "手动更新所有用户兴趣画像")
    @PostMapping("/updateAll")
    public Result<Void> updateAllUserInterests() {
        interestService.updateAllUserInterests();
        return Result.success();
    }
}
