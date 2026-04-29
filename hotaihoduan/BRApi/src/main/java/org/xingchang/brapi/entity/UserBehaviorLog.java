package org.xingchang.brapi.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户行为日志实体
 */
@Data
@TableName("user_behavior_log")
public class UserBehaviorLog {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    private Long contentId;
    private String actionType;  // view/click/like/collect/share/comment
    private Integer duration;   // 停留时长(秒)
    private String source;      // feed/search/profile/detail
    private String ip;
    private String device;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
