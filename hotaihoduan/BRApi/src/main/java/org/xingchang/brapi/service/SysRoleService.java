package org.xingchang.brapi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.xingchang.brapi.common.PageResult;
import org.xingchang.brapi.entity.SysRole;
import org.xingchang.brapi.mapper.SysRoleMapper;
import org.xingchang.brapi.util.PageUtils;

import java.util.Map;

@Service
public class SysRoleService extends ServiceImpl<SysRoleMapper, SysRole> {
    
    public PageResult<SysRole> getList(Map<String, Object> params) {
        Integer pageNum = PageUtils.getPageNum(params);
        Integer pageSize = PageUtils.getPageSize(params);
        String roleName = PageUtils.getString(params, "roleName");
        Integer status = PageUtils.getInteger(params, "status");
        
        Page<SysRole> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(roleName)) {
            wrapper.like(SysRole::getRoleName, roleName);
        }
        if (status != null) {
            wrapper.eq(SysRole::getStatus, status);
        }
        
        wrapper.orderByAsc(SysRole::getSort)
               .orderByDesc(SysRole::getCreateTime);
        
        IPage<SysRole> result = page(page, wrapper);
        return PageResult.of(result.getTotal(), result.getRecords());
    }
    
    public boolean updateStatus(Long id, Integer status) {
        SysRole role = new SysRole();
        role.setId(id);
        role.setStatus(status);
        return updateById(role);
    }
}
