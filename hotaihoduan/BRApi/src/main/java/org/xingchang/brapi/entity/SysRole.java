package org.xingchang.brapi.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色实体
 */
@Data
@TableName("sys_role")
public class SysRole {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String roleName;
    private String roleKey;
    private Integer sort;
    private Integer status;
    private String remark;
    
    @TableField(typeHandler = org.xingchang.brapi.config.JsonLongListTypeHandler.class)
    private List<Long> menuIds;
    
    private Integer dataScope;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
