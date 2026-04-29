package org.xingchang.brapi.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户点赞实体
 */
@Data
@TableName("user_like")
public class UserLike {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    private String userName;
    private String userAvatar;
    private String targetType;
    private Long targetId;
    private String targetTitle;
    private String targetAuthor;
    private Integer status;
    private String ip;
    private String device;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime likeTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime cancelTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @TableLogic
    private Integer deleted;
}
