package org.xingchang.brapi.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户分享实体
 */
@Data
@TableName("user_share")
public class UserShare {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    private String userName;
    private String userAvatar;
    private String targetTitle;
    private String platform;
    private Integer clickCount;
    private Integer conversionCount;
    private String ip;
    private String device;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime shareTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @TableLogic
    private Integer deleted;
}
