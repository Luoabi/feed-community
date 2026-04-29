package org.xingchang.brapi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.xingchang.brapi.entity.*;
import org.xingchang.brapi.mapper.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户兴趣画像服务
 */
@Service
public class UserInterestService extends ServiceImpl<UserInterestProfileMapper, UserInterestProfile> {
    
    @Autowired
    private UserBehaviorLogMapper behaviorLogMapper;
    
    @Autowired
    private ContentMapper contentMapper;
    
    @Autowired
    private ContentTagMapper contentTagMapper;
    
    @Autowired
    private TagMapper tagMapper;
    
    @Autowired
    private SysUserMapper userMapper;
    
    /**
     * 定时任务：每2小时更新所有用户的兴趣画像
     */
    @Scheduled(cron = "0 0 */2 * * ?")
    public void updateAllUserInterests() {
        System.out.println("开始更新用户兴趣画像...");
        
        // 获取所有用户ID
        List<SysUser> users = userMapper.selectList(
            new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getStatus, 1)
                .eq(SysUser::getDeleted, 0)
        );
        
        for (SysUser user : users) {
            try {
                updateUserInterest(user.getId());
            } catch (Exception e) {
                System.err.println("更新用户 " + user.getId() + " 兴趣画像失败: " + e.getMessage());
            }
        }
        
        System.out.println("用户兴趣画像更新完成，共更新 " + users.size() + " 个用户");
    }
    
    /**
     * 更新单个用户的兴趣画像
     */
    public void updateUserInterest(Long userId) {
        // 1. 获取用户最近30天的行为记录
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<UserBehaviorLog> behaviors = behaviorLogMapper.selectList(
            new LambdaQueryWrapper<UserBehaviorLog>()
                .eq(UserBehaviorLog::getUserId, userId)
                .ge(UserBehaviorLog::getCreateTime, thirtyDaysAgo)
        );
        
        if (behaviors.isEmpty()) {
            return;
        }
        
        // 2. 统计每个标签的行为数据
        Map<Long, TagInterestData> tagStatsMap = new HashMap<>();
        
        for (UserBehaviorLog behavior : behaviors) {
            // 获取内容的标签
            Content content = contentMapper.selectById(behavior.getContentId());
            if (content == null || content.getTags() == null) {
                continue;
            }
            
            // 解析标签（逗号分隔）
            String[] tags = content.getTags().split(",");
            for (String tagName : tags) {
                tagName = tagName.trim();
                if (tagName.isEmpty()) {
                    continue;
                }
                
                // ✅ 使用final变量以便在Lambda中使用
                final String finalTagName = tagName;
                
                // 查找标签ID
                Tag tag = tagMapper.selectOne(
                    new LambdaQueryWrapper<Tag>()
                        .eq(Tag::getTagName, finalTagName)
                );
                
                if (tag == null) {
                    continue;
                }
                
                // 累计行为数据
                TagInterestData data = tagStatsMap.computeIfAbsent(
                    tag.getId(), 
                    k -> new TagInterestData(tag.getId(), finalTagName)
                );
                
                switch (behavior.getActionType()) {
                    case "view":
                        data.viewCount++;
                        break;
                    case "like":
                        data.likeCount++;
                        break;
                    case "collect":
                        data.collectCount++;
                        break;
                    case "comment":
                        data.commentCount++;
                        break;
                    case "share":
                        data.shareCount++;
                        break;
                }
                
                data.lastActiveTime = behavior.getCreateTime();
            }
        }
        
        // 3. 计算兴趣分数并保存
        for (TagInterestData data : tagStatsMap.values()) {
            double score = calculateInterestScore(data);
            
            // 查询是否已存在
            UserInterestProfile existing = getOne(
                new LambdaQueryWrapper<UserInterestProfile>()
                    .eq(UserInterestProfile::getUserId, userId)
                    .eq(UserInterestProfile::getTagId, data.tagId)
            );
            
            if (existing != null) {
                // 更新
                existing.setInterestScore(score);
                existing.setViewCount(data.viewCount);
                existing.setLikeCount(data.likeCount);
                existing.setCollectCount(data.collectCount);
                existing.setCommentCount(data.commentCount);
                existing.setShareCount(data.shareCount);
                existing.setLastActiveTime(data.lastActiveTime);
                existing.setUpdateTime(LocalDateTime.now());
                updateById(existing);
            } else {
                // 新增
                UserInterestProfile profile = new UserInterestProfile();
                profile.setUserId(userId);
                profile.setTagId(data.tagId);
                profile.setTagName(data.tagName);
                profile.setInterestScore(score);
                profile.setViewCount(data.viewCount);
                profile.setLikeCount(data.likeCount);
                profile.setCollectCount(data.collectCount);
                profile.setCommentCount(data.commentCount);
                profile.setShareCount(data.shareCount);
                profile.setLastActiveTime(data.lastActiveTime);
                profile.setCreateTime(LocalDateTime.now());
                profile.setUpdateTime(LocalDateTime.now());
                save(profile);
            }
        }
    }
    
    /**
     * 计算兴趣分数
     * 行为权重：浏览(1) < 点赞(3) < 评论(4) < 收藏(5) < 分享(6)
     */
    private double calculateInterestScore(TagInterestData data) {
        double score = data.viewCount * 1.0
                     + data.likeCount * 3.0
                     + data.commentCount * 4.0
                     + data.collectCount * 5.0
                     + data.shareCount * 6.0;
        
        // 时间衰减（30天衰减50%）
        if (data.lastActiveTime != null) {
            long daysSinceLastActive = ChronoUnit.DAYS.between(
                data.lastActiveTime, LocalDateTime.now()
            );
            double timeDecay = Math.pow(0.5, daysSinceLastActive / 30.0);
            score = score * timeDecay;
        }
        
        // 归一化到0-100
        return Math.min(100.0, score);
    }
    
    /**
     * 获取用户的Top N兴趣标签
     */
    public List<UserInterestProfile> getTopInterests(Long userId, int topN) {
        return list(
            new LambdaQueryWrapper<UserInterestProfile>()
                .eq(UserInterestProfile::getUserId, userId)
                .orderByDesc(UserInterestProfile::getInterestScore)
                .last("LIMIT " + topN)
        );
    }
    
    /**
     * 获取用户的所有兴趣标签
     */
    public List<UserInterestProfile> getUserInterests(Long userId) {
        return list(
            new LambdaQueryWrapper<UserInterestProfile>()
                .eq(UserInterestProfile::getUserId, userId)
                .orderByDesc(UserInterestProfile::getInterestScore)
        );
    }
    
    /**
     * 批量设置用户兴趣（用于冷启动）
     */
    public boolean setUserInterests(Long userId, List<Long> tagIds) {
        // 删除旧的兴趣
        remove(new LambdaQueryWrapper<UserInterestProfile>()
            .eq(UserInterestProfile::getUserId, userId));
        
        // 添加新的兴趣
        for (Long tagId : tagIds) {
            Tag tag = tagMapper.selectById(tagId);
            if (tag == null) {
                continue;
            }
            
            UserInterestProfile profile = new UserInterestProfile();
            profile.setUserId(userId);
            profile.setTagId(tagId);
            profile.setTagName(tag.getTagName());
            profile.setInterestScore(50.0); // 初始分数50
            profile.setViewCount(0);
            profile.setLikeCount(0);
            profile.setCollectCount(0);
            profile.setCommentCount(0);
            profile.setShareCount(0);
            profile.setLastActiveTime(LocalDateTime.now());
            profile.setCreateTime(LocalDateTime.now());
            profile.setUpdateTime(LocalDateTime.now());
            save(profile);
        }
        
        return true;
    }
    
    /**
     * 标签兴趣数据内部类
     */
    private static class TagInterestData {
        Long tagId;
        String tagName;
        int viewCount = 0;
        int likeCount = 0;
        int collectCount = 0;
        int commentCount = 0;
        int shareCount = 0;
        LocalDateTime lastActiveTime;
        
        TagInterestData(Long tagId, String tagName) {
            this.tagId = tagId;
            this.tagName = tagName;
        }
    }
}
