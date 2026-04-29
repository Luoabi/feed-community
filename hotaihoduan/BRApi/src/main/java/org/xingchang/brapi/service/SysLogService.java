package org.xingchang.brapi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.xingchang.brapi.common.PageResult;
import org.xingchang.brapi.entity.SysLog;
import org.xingchang.brapi.mapper.SysLogMapper;
import org.xingchang.brapi.util.PageUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * 操作日志Service
 */
@Service
public class SysLogService extends ServiceImpl<SysLogMapper, SysLog> {
    
    public PageResult<SysLog> getList(Map<String, Object> params) {
        Integer pageNum = PageUtils.getPageNum(params);
        Integer pageSize = PageUtils.getPageSize(params);
        String operator = PageUtils.getString(params, "operator");
        String operationType = PageUtils.getString(params, "operationType");
        String module = PageUtils.getString(params, "module");
        String startDate = PageUtils.getString(params, "startDate");
        String endDate = PageUtils.getString(params, "endDate");
        
        Page<SysLog> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysLog> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(operator)) {
            wrapper.like(SysLog::getUsername, operator);
        }
        if (StringUtils.hasText(operationType)) {
            wrapper.eq(SysLog::getOperationType, operationType);
        }
        if (StringUtils.hasText(module)) {
            wrapper.like(SysLog::getModule, module);
        }
        if (StringUtils.hasText(startDate)) {
            LocalDateTime start = LocalDateTime.parse(startDate + " 00:00:00", 
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            wrapper.ge(SysLog::getCreateTime, start);
        }
        if (StringUtils.hasText(endDate)) {
            LocalDateTime end = LocalDateTime.parse(endDate + " 23:59:59", 
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            wrapper.le(SysLog::getCreateTime, end);
        }
        
        wrapper.orderByDesc(SysLog::getCreateTime);
        
        IPage<SysLog> result = page(page, wrapper);
        return PageResult.of(result.getTotal(), result.getRecords());
    }
    
    /**
     * 清空日志
     */
    public boolean clearAll() {
        return remove(new LambdaQueryWrapper<>());
    }
}
