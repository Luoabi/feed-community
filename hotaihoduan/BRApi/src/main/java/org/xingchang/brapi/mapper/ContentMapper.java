package org.xingchang.brapi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.xingchang.brapi.entity.Content;

/**
 * 内容Mapper
 */
@Mapper
public interface ContentMapper extends BaseMapper<Content> {
    // BaseMapper已提供基础CRUD方法
}
