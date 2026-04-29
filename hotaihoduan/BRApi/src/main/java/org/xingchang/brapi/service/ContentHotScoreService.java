package org.xingchang.brapi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.xingchang.brapi.entity.Content;
import org.xingchang.brapi.mapper.ContentMapper;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * 内容热度分数计算服务
 */
@Service
public class ContentHotScoreService {
    
    @Autowired
    private ContentMapper contentMapper;
    
    /**
     * 定时任务：每小时更新内容热度分数
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void updateAllHotScores() {
        System.out.println("开始更新内容热度分数...");
        
        // 只更新最近30天的已发布内容
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<Content> contents = contentMapper.selectList(
            new LambdaQueryWrapper<Content>()
                .eq(Content::getStatus, "published")
                .eq(Content::getDeleted, 0)
                .ge(Content::getPublishTime, thirtyDaysAgo)
        );
        
        int updatedCount = 0;
        for (Content content : contents) {
            try {
                double hotScore = calculateHotScore(content);
                content.setHotScore(hotScore);
                contentMapper.updateById(content);
                updatedCount++;
            } catch (Exception e) {
                System.err.println("更新内容 " + content.getId() + " 热度分数失败: " + e.getMessage());
            }
        }
        
        System.out.println("内容热度分数更新完成，共更新 " + updatedCount + " 条内容");
    }
    
    /**
     * 计算单个内容的热度分数
     * 使用威尔逊热度算法 + 时间衰减
     */
    public double calculateHotScore(Content content) {
        // 1. 互动分数（基础分）
        int views = content.getViews() != null ? content.getViews() : 0;
        int likes = content.getLikes() != null ? content.getLikes() : 0;
        int comments = content.getComments() != null ? content.getComments() : 0;
        int shares = content.getShares() != null ? content.getShares() : 0;
        int collects = content.getCollects() != null ? content.getCollects() : 0;
        
        double interactionScore = views * 0.1
                                + likes * 2
                                + comments * 3
                                + shares * 4
                                + collects * 5;
        
        // 2. 时间衰减因子
        double timeDecay = calculateTimeDecay(content.getPublishTime());
        
        // 3. 质量加权
        double qualityWeight = calculateQualityWeight(content);
        
        // 4. 最终热度分数
        return interactionScore * timeDecay * qualityWeight;
    }
    
    /**
     * 计算时间衰减因子
     * 24小时内无衰减，之后每24小时衰减50%
     */
    private double calculateTimeDecay(LocalDateTime publishTime) {
        if (publishTime == null) {
            return 0.1;
        }
        
        long hours = ChronoUnit.HOURS.between(publishTime, LocalDateTime.now());
        
        if (hours <= 24) {
            return 1.0; // 24小时内无衰减
        } else {
            // 每24小时衰减50%
            double days = hours / 24.0;
            return Math.pow(0.5, days - 1);
        }
    }
    
    /**
     * 计算质量权重
     */
    private double calculateQualityWeight(Content content) {
        double weight = 1.0;
        
        // 精华内容加权
        if (content.getIsEssence() != null && content.getIsEssence() == 1) {
            weight *= 1.5;
        }
        
        // 推荐内容加权
        if (content.getIsRecommend() != null && content.getIsRecommend() == 1) {
            weight *= 1.3;
        }
        
        // 置顶内容加权
        if (content.getIsTop() != null && content.getIsTop() == 1) {
            weight *= 2.0;
        }
        
        return weight;
    }
    
    /**
     * 手动更新单个内容的热度分数
     */
    public boolean updateContentHotScore(Long contentId) {
        Content content = contentMapper.selectById(contentId);
        if (content == null) {
            return false;
        }
        
        double hotScore = calculateHotScore(content);
        content.setHotScore(hotScore);
        return contentMapper.updateById(content) > 0;
    }
}
