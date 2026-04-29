package org.xingchang.brapi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.xingchang.brapi.common.PageResult;
import org.xingchang.brapi.common.RoleConstants;
import org.xingchang.brapi.entity.SysUser;
import org.xingchang.brapi.mapper.SysUserMapper;
import org.xingchang.brapi.util.PageUtils;
import org.xingchang.brapi.util.PasswordUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 用户Service
 */
@Service
public class SysUserService extends ServiceImpl<SysUserMapper, SysUser> {
    
    public SysUser getByUsername(String username) {
        return lambdaQuery()
                .eq(SysUser::getUsername, username)
                .one();
    }
    
    public PageResult<SysUser> getList(Map<String, Object> params) {
        Integer pageNum = PageUtils.getPageNum(params);
        Integer pageSize = PageUtils.getPageSize(params);
        String username = PageUtils.getString(params, "username");
        Integer status = PageUtils.getInteger(params, "status");
        
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(username)) {
            wrapper.like(SysUser::getUsername, username);
        }
        if (status != null) {
            wrapper.eq(SysUser::getStatus, status);
        }
        
        wrapper.orderByDesc(SysUser::getCreateTime);
        
        IPage<SysUser> result = page(page, wrapper);
        return PageResult.of(result.getTotal(), result.getRecords());
    }
    
    public boolean resetPassword(Long id, String password) {
        SysUser user = new SysUser();
        user.setId(id);
        user.setPassword(password);
        return updateById(user);
    }
    
    public boolean updateStatus(Long id, Integer status) {
        SysUser user = new SysUser();
        user.setId(id);
        user.setStatus(status);
        return updateById(user);
    }
    
    public boolean register(String username, String password, String nickname, String phone, String email) {
        // 检查用户名是否已存在
        SysUser existUser = getByUsername(username);
        if (existUser != null) {
            return false;
        }
        
        // 创建新用户
        SysUser user = new SysUser();
        user.setUsername(username);
        user.setPassword(PasswordUtil.encode(password)); // ✅ 使用BCrypt加密密码
        user.setNickname(nickname);
        user.setPhone(phone);
        user.setEmail(email);
        user.setStatus(1); // 默认启用
        user.setGender(0); // 默认未知
        
        // ✅ 设置默认角色为普通用户 (role_id = 4)
        user.setRoleIds(RoleConstants.DEFAULT_ROLE_ID_LIST); // List<Long>: [4L]
        user.setRoleNames(RoleConstants.DEFAULT_ROLE_NAMES); // "普通用户"
        
        return save(user);
    }
}
