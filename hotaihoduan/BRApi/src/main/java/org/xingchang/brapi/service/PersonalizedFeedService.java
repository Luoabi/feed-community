package org.xingchang.brapi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xingchang.brapi.entity.Content;
import org.xingchang.brapi.entity.UserBehaviorLog;
import org.xingchang.brapi.entity.UserInterestProfile;
import org.xingchang.brapi.mapper.ContentMapper;
import org.xingchang.brapi.mapper.UserBehaviorLogMapper;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 个性化Feed流推荐服务
 */
@Service
public class PersonalizedFeedService {
    
    @Autowired
    private UserInterestService interestService;
    
    @Autowired
    private ContentMapper contentMapper;
    
    @Autowired
    private UserBehaviorLogMapper behaviorLogMapper;
    
    /**
     * 获取个性化Feed流
     */
    public List<Content> getPersonalizedFeed(Long userId, int page, int size) {
        // 1. 获取用户Top5兴趣标签
        List<UserInterestProfile> interests = interestService.getTopInterests(userId, 5);
        
        // 如果用户没有兴趣画像，返回热门内容（冷启动）
        if (interests.isEmpty()) {
            return getHotContents(page, size);
        }
        
        // 2. 获取候选内容（最近7天的已发布内容）
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        List<Content> candidates = contentMapper.selectList(
            new LambdaQueryWrapper<Content>()
                .eq(Content::getStatus, "published")
                .eq(Content::getDeleted, 0)
                .ge(Content::getPublishTime, sevenDaysAgo)
                .orderByDesc(Content::getPublishTime)
                .last("LIMIT 500") // 候选池最多500条
        );
        
        if (candidates.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 3. 计算个性化分数
        List<ContentWithScore> scoredContents = new ArrayList<>();
        for (Content content : candidates) {
            double score = calculatePersonalizedScore(content, interests, userId);
            if (score > 0) {
                scoredContents.add(new ContentWithScore(content, score));
            }
        }
        
        // 4. 排序 + 去重 + 多样性 + 分页
        return scoredContents.stream()
                .sorted(Comparator.comparing(ContentWithScore::getScore).reversed())
                .map(ContentWithScore::getContent)
                .filter(c -> !hasUserViewed(userId, c.getId())) // 去重：过滤已浏览的
                .skip((long) (page - 1) * size)
                .limit(size)
                .collect(Collectors.toList());
    }
    
    /**
     * 计算个性化推荐分数
     */
    private double calculatePersonalizedScore(Content content, 
                                             List<UserInterestProfile> interests, 
                                             Long userId) {
        double score = 0;
        
        // 1. 兴趣匹配分（50%）
        if (content.getTags() != null && !content.getTags().isEmpty()) {
            String[] contentTags = content.getTags().split(",");
            double maxInterestScore = 0;
            
            for (UserInterestProfile interest : interests) {
                for (String tag : contentTags) {
                    if (tag.trim().equals(interest.getTagName())) {
                        maxInterestScore = Math.max(maxInterestScore, interest.getInterestScore());
                        break;
                    }
                }
            }
            
            score += maxInterestScore * 0.5;
        }
        
        // 2. 内容热度分（30%）
        double hotScore = calculateHotScore(content);
        score += hotScore * 0.3;
        
        // 3. 时间新鲜度（15%）
        double freshnessScore = calculateFreshnessScore(content);
        score += freshnessScore * 15;
        
        // 4. 内容质量分（5%）
        double qualityScore = calculateQualityScore(content);
        score += qualityScore * 5;
        
        return score;
    }
    
    /**
     * 计算内容热度分数
     */
    private double calculateHotScore(Content content) {
        // 优先使用预计算的热度分数
        if (content.getHotScore() != null && content.getHotScore() > 0) {
            return Math.min(30.0, content.getHotScore() / 10.0);
        }
        
        // 如果没有预计算分数，实时计算
        double interactionScore = (content.getViews() != null ? content.getViews() : 0) * 0.1
                                + (content.getLikes() != null ? content.getLikes() : 0) * 2
                                + (content.getComments() != null ? content.getComments() : 0) * 3
                                + (content.getShares() != null ? content.getShares() : 0) * 4
                                + (content.getCollects() != null ? content.getCollects() : 0) * 5;
        
        // 归一化到0-30
        return Math.min(30.0, interactionScore / 10.0);
    }
    
    /**
     * 计算时间新鲜度分数
     */
    private double calculateFreshnessScore(Content content) {
        if (content.getPublishTime() == null) {
            return 0;
        }
        
        long hoursSincePublish = ChronoUnit.HOURS.between(
            content.getPublishTime(), LocalDateTime.now()
        );
        
        // 24小时内满分，之后线性衰减
        if (hoursSincePublish <= 24) {
            return 100.0;
        } else if (hoursSincePublish <= 168) { // 7天
            return 100.0 - (hoursSincePublish - 24) * 0.5;
        } else {
            return 0;
        }
    }
    
    /**
     * 计算内容质量分数
     */
    private double calculateQualityScore(Content content) {
        double score = 0;
        
        // 精华内容加分
        if (content.getIsEssence() != null && content.getIsEssence() == 1) {
            score += 50;
        }
        
        // 推荐内容加分
        if (content.getIsRecommend() != null && content.getIsRecommend() == 1) {
            score += 30;
        }
        
        // 有封面图加分
        if (content.getCoverImage() != null && !content.getCoverImage().isEmpty()) {
            score += 20;
        }
        
        return score;
    }
    
    /**
     * 检查用户是否已浏览过该内容
     */
    private boolean hasUserViewed(Long userId, Long contentId) {
        Long count = behaviorLogMapper.selectCount(
            new LambdaQueryWrapper<UserBehaviorLog>()
                .eq(UserBehaviorLog::getUserId, userId)
                .eq(UserBehaviorLog::getContentId, contentId)
                .eq(UserBehaviorLog::getActionType, "view")
        );
        return count != null && count > 0;
    }
    
    /**
     * 获取热门内容（冷启动）
     */
    private List<Content> getHotContents(int page, int size) {
        return contentMapper.selectList(
            new LambdaQueryWrapper<Content>()
                .eq(Content::getStatus, "published")
                .eq(Content::getDeleted, 0)
                .orderByDesc(Content::getIsTop)
                .orderByDesc(Content::getIsRecommend)
                .orderByDesc(Content::getIsHot)
                .orderByDesc(Content::getViews)
                .orderByDesc(Content::getLikes)
                .orderByDesc(Content::getPublishTime)
                .last("LIMIT " + ((page - 1) * size) + ", " + size)
        );
    }
    
    /**
     * 内容+分数包装类
     */
    private static class ContentWithScore {
        private final Content content;
        private final double score;
        
        ContentWithScore(Content content, double score) {
            this.content = content;
            this.score = score;
        }
        
        public Content getContent() {
            return content;
        }
        
        public double getScore() {
            return score;
        }
    }
}
