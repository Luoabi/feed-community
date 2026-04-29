package org.xingchang.brapi.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 部门实体
 */
@Data
@TableName("sys_dept")
public class SysDept {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long parentId;
    private String deptName;
    private String leader;
    private String phone;
    private String email;
    private Integer sort;
    private Integer status;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
    
    // 子部门列表
    @TableField(exist = false)
    private List<SysDept> children;
}
