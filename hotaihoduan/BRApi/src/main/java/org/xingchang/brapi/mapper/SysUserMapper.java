package org.xingchang.brapi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.xingchang.brapi.entity.SysUser;

/**
 * 用户Mapper
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
    // BaseMapper已提供基础CRUD方法，无需再定义
    // 如需自定义SQL，可在这里添加方法
}
