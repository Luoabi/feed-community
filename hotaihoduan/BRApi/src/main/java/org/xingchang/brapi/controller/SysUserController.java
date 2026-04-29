package org.xingchang.brapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.xingchang.brapi.annotation.OperationLog;
import org.xingchang.brapi.common.PageResult;
import org.xingchang.brapi.common.Result;
import org.xingchang.brapi.dto.LoginRequest;
import org.xingchang.brapi.dto.LoginResponse;
import org.xingchang.brapi.dto.RegisterRequest;
import org.xingchang.brapi.entity.SysUser;
import org.xingchang.brapi.service.SysUserService;
import org.xingchang.brapi.util.JwtUtil;
import org.xingchang.brapi.util.PasswordUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户Controller
 */
@Tag(name = "用户管理")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class SysUserController {
    
    private final SysUserService userService;
    private final JwtUtil jwtUtil;
    
    @Operation(summary = "用户登录")
    @OperationLog(module = "用户管理", operationType = "login", description = "用户登录")
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request) {
        // 查询用户
        SysUser user = userService.getByUsername(request.getUsername());
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        // ✅ 验证密码（使用BCrypt）
        if (!PasswordUtil.matches(request.getPassword(), user.getPassword())) {
            return Result.error("密码错误");
        }
        
        // 检查用户状态
        if (user.getStatus() == 0) {
            return Result.error("账号已被禁用");
        }
        
        // ✅ 检查用户角色：普通用户不能登录后台管理系统
        if (user.getRoleIds() != null && user.getRoleIds().contains(4L) && user.getRoleIds().size() == 1) {
            return Result.error("普通用户无权限登录后台管理系统");
        }
        
        // ✅ 生成JWT Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        
        // 隐藏密码
        user.setPassword(null);
        
        // 获取角色列表（从roleNames转换）
        String[] roles = user.getRoleNames() != null ? user.getRoleNames().split(",") : new String[]{"user"};
        
        LoginResponse response = new LoginResponse(token, user, Arrays.asList(roles));
        return Result.success(response);
    }
    
    @Operation(summary = "用户注册")
    @OperationLog(module = "用户管理", operationType = "register", description = "用户注册")
    @PostMapping("/register")
    public Result<Void> register(@RequestBody RegisterRequest request) {
        // ✅ 验证必填字段
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            return Result.error("用户名不能为空");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            return Result.error("密码不能为空");
        }
        if (request.getNickname() == null || request.getNickname().trim().isEmpty()) {
            return Result.error("昵称不能为空");
        }
        
        // ✅ 验证用户名格式：3-20个字符，只能包含字母、数字、下划线
        if (request.getUsername().length() < 3 || request.getUsername().length() > 20) {
            return Result.error("用户名长度为3-20个字符");
        }
        if (!request.getUsername().matches("^[a-zA-Z0-9_]+$")) {
            return Result.error("用户名只能包含字母、数字、下划线");
        }
        
        // ✅ 验证密码长度：6-20个字符
        if (request.getPassword().length() < 6 || request.getPassword().length() > 20) {
            return Result.error("密码长度为6-20个字符");
        }
        
        // ✅ 验证昵称长度：1-20个字符
        if (request.getNickname().length() > 20) {
            return Result.error("昵称最多20个字符");
        }
        
        // ✅ 验证手机号格式（如果提供）
        if (request.getPhone() != null && !request.getPhone().trim().isEmpty()) {
            if (!request.getPhone().matches("^1[3-9]\\d{9}$")) {
                return Result.error("手机号格式不正确");
            }
        }
        
        // 注册用户（Service中会加密密码）
        boolean success = userService.register(
            request.getUsername().trim(),
            request.getPassword(),
            request.getNickname().trim(),
            request.getPhone() != null ? request.getPhone().trim() : null,
            null // 邮箱不再使用
        );
        
        if (success) {
            return Result.success();
        } else {
            return Result.error("用户名已存在");
        }
    }
    
    @Operation(summary = "获取用户信息")
    @GetMapping("/info")
    public Result<Map<String, Object>> getUserInfo(HttpServletRequest request) {
        // ✅ 从请求中获取用户ID（由JWT拦截器设置）
        Long userId = (Long) request.getAttribute("userId");
        
        SysUser user = userService.getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        user.setPassword(null);
        
        // 获取角色列表
        String[] roles = user.getRoleNames() != null ? user.getRoleNames().split(",") : new String[]{"user"};
        
        Map<String, Object> data = new HashMap<>();
        data.put("userInfo", user);
        data.put("roles", Arrays.asList(roles));
        
        return Result.success(data);
    }
    
    @Operation(summary = "用户列表")
    @OperationLog(module = "用户管理", operationType = "query", description = "查询用户列表")
    @GetMapping("/list")
    public Result<PageResult<SysUser>> list(@RequestParam Map<String, Object> params) {
        PageResult<SysUser> result = userService.getList(params);
        // 隐藏密码
        result.getList().forEach(user -> user.setPassword(null));
        return Result.success(result);
    }
    
    @Operation(summary = "新增用户")
    @OperationLog(module = "用户管理", operationType = "create", description = "新增用户")
    @PostMapping("/add")
    public Result<Void> add(@RequestBody SysUser user) {
        // ✅ 加密密码
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(PasswordUtil.encode(user.getPassword()));
        }
        boolean success = userService.save(user);
        return success ? Result.success() : Result.error();
    }
    
    @Operation(summary = "更新用户")
    @OperationLog(module = "用户管理", operationType = "update", description = "更新用户")
    @PutMapping("/update")
    public Result<Void> update(@RequestBody SysUser user, HttpServletRequest request) {
        // ✅ 如果前端没有传递用户ID，从JWT Token中获取（用于小程序个人信息更新）
        if (user.getId() == null) {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return Result.error("用户ID不能为空");
            }
            user.setId(userId);
        }
        
        // ✅ 验证用户是否存在
        SysUser existUser = userService.getById(user.getId());
        if (existUser == null) {
            return Result.error("用户不存在");
        }
        
        // 如果包含密码，需要加密
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(PasswordUtil.encode(user.getPassword()));
        } else {
            // ✅ 如果没有传递密码，设置为null（MyBatis-Plus会跳过null字段）
            user.setPassword(null);
        }
        
        // ✅ 确保不会覆盖关键字段（如果前端没传）
        if (user.getUsername() == null) {
            user.setUsername(null); // 不更新
        }
        if (user.getRoleIds() == null) {
            user.setRoleIds(null); // 不更新
        }
        if (user.getStatus() == null) {
            user.setStatus(null); // 不更新
        }
        
        boolean success = userService.updateById(user);
        return success ? Result.success() : Result.error();
    }
    
    @Operation(summary = "删除用户")
    @OperationLog(module = "用户管理", operationType = "delete", description = "删除用户")
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        boolean success = userService.removeById(id);
        return success ? Result.success() : Result.error();
    }
    
    @Operation(summary = "重置密码")
    @OperationLog(module = "用户管理", operationType = "update", description = "重置用户密码")
    @PutMapping("/resetPassword")
    public Result<Void> resetPassword(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        // ✅ 判断是管理员重置密码还是用户自己修改密码
        if (params.containsKey("id")) {
            // 管理员重置用户密码为默认密码
            Long id = Long.valueOf(params.get("id").toString());
            String password = "123456"; // 默认密码
            String encodedPassword = PasswordUtil.encode(password);
            boolean success = userService.resetPassword(id, encodedPassword);
            return success ? Result.success() : Result.error();
        } else if (params.containsKey("oldPassword") && params.containsKey("newPassword")) {
            // 用户自己修改密码
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return Result.error("用户未登录");
            }
            
            String oldPassword = params.get("oldPassword").toString();
            String newPassword = params.get("newPassword").toString();
            
            // 验证新密码长度
            if (newPassword.length() < 6 || newPassword.length() > 20) {
                return Result.error("密码长度为6-20个字符");
            }
            
            // 获取用户信息
            SysUser user = userService.getById(userId);
            if (user == null) {
                return Result.error("用户不存在");
            }
            
            // 验证旧密码
            if (!PasswordUtil.matches(oldPassword, user.getPassword())) {
                return Result.error("原密码错误");
            }
            
            // 更新密码
            String encodedPassword = PasswordUtil.encode(newPassword);
            boolean success = userService.resetPassword(userId, encodedPassword);
            return success ? Result.success() : Result.error("密码修改失败");
        } else {
            return Result.error("参数错误");
        }
    }
    
    @Operation(summary = "修改状态")
    @OperationLog(module = "用户管理", operationType = "update", description = "修改用户状态")
    @PutMapping("/changeStatus")
    public Result<Void> changeStatus(@RequestBody Map<String, Object> params) {
        Long id = Long.valueOf(params.get("id").toString());
        Integer status = (Integer) params.get("status");
        boolean success = userService.updateStatus(id, status);
        return success ? Result.success() : Result.error();
    }
    
    @Operation(summary = "退出登录")
    @OperationLog(module = "用户管理", operationType = "logout", description = "用户退出登录")
    @PostMapping("/logout")
    public Result<String> logout() {
        // 前端删除Token即可，后端无需处理
        return Result.success("退出成功");
    }
}
