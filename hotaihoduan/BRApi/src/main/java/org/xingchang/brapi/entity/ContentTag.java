package org.xingchang.brapi.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 内容标签关联实体
 */
@Data
@TableName("content_tag")
public class ContentTag {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long contentId;
    private Long tagId;
    private String tagName;
    private Double weight;  // 标签权重(0-1)
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
