package org.xingchang.brapi.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 内容分类实体
 */
@Data
@TableName("content_category")
public class ContentCategory {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String categoryName;
    private String categoryKey;
    private String icon;
    private Integer sort;
    private Integer contentCount;
    private String description;
    private Integer status;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
