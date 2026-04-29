package org.xingchang.brapi.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.xingchang.brapi.entity.UserBehaviorLog;
import org.xingchang.brapi.mapper.UserBehaviorLogMapper;

import java.time.LocalDateTime;

/**
 * 用户行为追踪服务
 */
@Service
public class UserBehaviorService extends ServiceImpl<UserBehaviorLogMapper, UserBehaviorLog> {
    
    /**
     * 记录用户行为
     */
    public boolean trackBehavior(Long userId, Long contentId, String actionType, 
                                Integer duration, String source, String ip, String device) {
        UserBehaviorLog log = new UserBehaviorLog();
        log.setUserId(userId);
        log.setContentId(contentId);
        log.setActionType(actionType);
        log.setDuration(duration);
        log.setSource(source);
        log.setIp(ip);
        log.setDevice(device);
        log.setCreateTime(LocalDateTime.now());
        
        return save(log);
    }
    
    /**
     * 记录浏览行为
     */
    public boolean trackView(Long userId, Long contentId, Integer duration, String source) {
        return trackBehavior(userId, contentId, "view", duration, source, null, null);
    }
    
    /**
     * 记录点击行为
     */
    public boolean trackClick(Long userId, Long contentId, String source) {
        return trackBehavior(userId, contentId, "click", 0, source, null, null);
    }
    
    /**
     * 记录点赞行为
     */
    public boolean trackLike(Long userId, Long contentId) {
        return trackBehavior(userId, contentId, "like", 0, null, null, null);
    }
    
    /**
     * 记录收藏行为
     */
    public boolean trackCollect(Long userId, Long contentId) {
        return trackBehavior(userId, contentId, "collect", 0, null, null, null);
    }
    
    /**
     * 记录分享行为
     */
    public boolean trackShare(Long userId, Long contentId) {
        return trackBehavior(userId, contentId, "share", 0, null, null, null);
    }
    
    /**
     * 记录评论行为
     */
    public boolean trackComment(Long userId, Long contentId) {
        return trackBehavior(userId, contentId, "comment", 0, null, null, null);
    }
}
