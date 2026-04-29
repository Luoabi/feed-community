package org.xingchang.brapi.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 内容实体（统一：合并了原content和post表）
 */
@Data
@TableName("content")
public class Content {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    // 基础信息
    private String title;
    private String content;
    private String summary;
    
    // 类型和来源
    private String contentType;  // article/post/image/video/link
    private String contentSource; // admin/user
    
    // 分类信息
    private Long categoryId;
    private String categoryName;
    
    // 媒体资源
    private String coverImage;
    
    // ✅ 图片列表（JSON数组）- 使用自定义 TypeHandler 处理 MySQL JSON 类型
    @TableField(typeHandler = org.xingchang.brapi.config.JsonStringListTypeHandler.class)
    private List<String> images;
    
    private String videoUrl;
    private String linkUrl;
    
    // 作者信息
    private Long authorId;
    private String authorName;
    private String authorAvatar;
    
    // 互动数据
    private Integer views;
    private Integer likes;
    private Integer comments;
    private Integer shares;
    private Integer collects;
    
    // 状态标记
    private String status;  // draft/pending/published/rejected
    private Integer isTop;
    private Integer isEssence;  // 新增：是否精华
    private Integer isHot;
    private Integer isRecommend;
    private Double hotScore;  // 新增：热度分数
    
    // 其他
    private String tags;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
