package org.xingchang.brapi.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志实体
 */
@Data
@TableName("sys_log")
public class SysLog {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    // 操作人信息
    private Long userId;
    private String username;
    
    // 操作信息
    private String module;              // 模块名称
    private String operationType;       // 操作类型：create/update/delete/query/login/logout
    private String description;         // 操作描述
    
    // 请求信息
    private String method;              // 请求方法：GET/POST/PUT/DELETE
    private String url;                 // 请求URL
    private String params;              // 请求参数
    
    // 响应信息
    private Integer status;             // 状态：1-成功，0-失败
    private String errorMsg;            // 错误信息
    private Long duration;              // 执行时长（毫秒）
    
    // 客户端信息
    private String ip;                  // IP地址
    private String browser;             // 浏览器
    private String os;                  // 操作系统
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
