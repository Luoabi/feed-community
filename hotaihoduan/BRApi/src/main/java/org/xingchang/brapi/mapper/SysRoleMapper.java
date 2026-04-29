package org.xingchang.brapi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.xingchang.brapi.entity.SysRole;

/**
 * 角色Mapper
 */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {
    // BaseMapper已提供基础CRUD方法
}
