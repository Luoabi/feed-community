package org.xingchang.brapi.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户个性化Feed流实体
 */
@Data
@TableName("user_feed")
public class UserFeed {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    private Long contentId;
    private Double score;       // 推荐分数
    private String reason;      // 推荐理由
    private Integer position;   // 推荐位置
    private Integer isViewed;   // 是否已浏览：0否 1是
    private Integer isClicked;  // 是否已点击：0否 1是
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireTime;
}
