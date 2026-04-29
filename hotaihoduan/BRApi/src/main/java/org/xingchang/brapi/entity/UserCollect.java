package org.xingchang.brapi.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户收藏实体
 */
@Data
@TableName("user_collect")
public class UserCollect {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    private String userName;
    private String userAvatar;
    private String targetType;
    private Long targetId;
    private String targetTitle;
    private String targetAuthor;
    private String folderName;
    private Integer status;
    private String ip;
    private String device;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime collectTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @TableLogic
    private Integer deleted;
}
