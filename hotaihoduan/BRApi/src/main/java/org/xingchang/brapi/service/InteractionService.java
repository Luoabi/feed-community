package org.xingchang.brapi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.xingchang.brapi.common.PageResult;
import org.xingchang.brapi.entity.Content;
import org.xingchang.brapi.entity.UserCollect;
import org.xingchang.brapi.entity.UserLike;
import org.xingchang.brapi.entity.UserShare;
import org.xingchang.brapi.mapper.ContentMapper;
import org.xingchang.brapi.mapper.UserCollectMapper;
import org.xingchang.brapi.mapper.UserLikeMapper;
import org.xingchang.brapi.mapper.UserShareMapper;
import org.xingchang.brapi.util.PageUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 互动数据Service
 */
@Service
public class InteractionService {
    
    @Autowired
    private UserLikeMapper userLikeMapper;
    
    @Autowired
    private UserCollectMapper userCollectMapper;
    
    @Autowired
    private UserShareMapper userShareMapper;
    
    @Autowired
    private ContentMapper contentMapper;
    
    /**
     * 获取点赞列表
     */
    public PageResult<UserLike> getLikeList(Map<String, Object> params) {
        Integer pageNum = PageUtils.getPageNum(params);
        Integer pageSize = PageUtils.getPageSize(params);
        String userName = (String) params.get("userName");
        String targetType = (String) params.get("targetType");
        String startDate = (String) params.get("startDate");
        String endDate = (String) params.get("endDate");
        
        Page<UserLike> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<UserLike> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(userName)) {
            wrapper.like(UserLike::getUserName, userName);
        }
        if (StringUtils.hasText(targetType)) {
            wrapper.eq(UserLike::getTargetType, targetType);
        }
        if (StringUtils.hasText(startDate)) {
            wrapper.ge(UserLike::getLikeTime, startDate + " 00:00:00");
        }
        if (StringUtils.hasText(endDate)) {
            wrapper.le(UserLike::getLikeTime, endDate + " 23:59:59");
        }
        
        wrapper.orderByDesc(UserLike::getLikeTime);
        
        IPage<UserLike> result = userLikeMapper.selectPage(page, wrapper);
        return PageResult.of(result.getTotal(), result.getRecords());
    }
    
    /**
     * 获取收藏列表
     */
    public PageResult<UserCollect> getCollectList(Map<String, Object> params) {
        Integer pageNum = PageUtils.getPageNum(params);
        Integer pageSize = PageUtils.getPageSize(params);
        String userName = (String) params.get("userName");
        String targetType = (String) params.get("targetType");
        String startDate = (String) params.get("startDate");
        String endDate = (String) params.get("endDate");
        
        Page<UserCollect> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<UserCollect> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(userName)) {
            wrapper.like(UserCollect::getUserName, userName);
        }
        if (StringUtils.hasText(targetType)) {
            wrapper.eq(UserCollect::getTargetType, targetType);
        }
        if (StringUtils.hasText(startDate)) {
            wrapper.ge(UserCollect::getCollectTime, startDate + " 00:00:00");
        }
        if (StringUtils.hasText(endDate)) {
            wrapper.le(UserCollect::getCollectTime, endDate + " 23:59:59");
        }
        
        wrapper.orderByDesc(UserCollect::getCollectTime);
        
        IPage<UserCollect> result = userCollectMapper.selectPage(page, wrapper);
        return PageResult.of(result.getTotal(), result.getRecords());
    }
    
    /**
     * 获取分享列表
     */
    public PageResult<UserShare> getShareList(Map<String, Object> params) {
        Integer pageNum = PageUtils.getPageNum(params);
        Integer pageSize = PageUtils.getPageSize(params);
        String userName = (String) params.get("userName");
        String platform = (String) params.get("platform");
        String startDate = (String) params.get("startDate");
        String endDate = (String) params.get("endDate");
        
        Page<UserShare> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<UserShare> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(userName)) {
            wrapper.like(UserShare::getUserName, userName);
        }
        if (StringUtils.hasText(platform)) {
            wrapper.eq(UserShare::getPlatform, platform);
        }
        if (StringUtils.hasText(startDate)) {
            wrapper.ge(UserShare::getShareTime, startDate + " 00:00:00");
        }
        if (StringUtils.hasText(endDate)) {
            wrapper.le(UserShare::getShareTime, endDate + " 23:59:59");
        }
        
        wrapper.orderByDesc(UserShare::getShareTime);
        
        IPage<UserShare> result = userShareMapper.selectPage(page, wrapper);
        return PageResult.of(result.getTotal(), result.getRecords());
    }
    
    /**
     * 获取统计数据
     */
    public Map<String, Object> getStats() {
        // 今天的开始和结束时间
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        
        // 总数统计
        Long totalLikes = userLikeMapper.selectCount(null);
        Long totalCollects = userCollectMapper.selectCount(null);
        Long totalShares = userShareMapper.selectCount(null);
        
        // 今日统计
        Long todayLikes = userLikeMapper.selectCount(
            new LambdaQueryWrapper<UserLike>()
                .ge(UserLike::getLikeTime, todayStart)
                .le(UserLike::getLikeTime, todayEnd)
        );
        
        Long todayCollects = userCollectMapper.selectCount(
            new LambdaQueryWrapper<UserCollect>()
                .ge(UserCollect::getCollectTime, todayStart)
                .le(UserCollect::getCollectTime, todayEnd)
        );
        
        Long todayShares = userShareMapper.selectCount(
            new LambdaQueryWrapper<UserShare>()
                .ge(UserShare::getShareTime, todayStart)
                .le(UserShare::getShareTime, todayEnd)
        );
        
        return Map.of(
            "totalLikes", totalLikes,
            "totalCollects", totalCollects,
            "totalShares", totalShares,
            "todayLikes", todayLikes,
            "todayCollects", todayCollects,
            "todayShares", todayShares
        );
    }
    
    /**
     * 获取我的点赞（优化版）
     */
    public PageResult<Map<String, Object>> getMyLikes(Map<String, Object> params) {
        Integer pageNum = PageUtils.getPageNum(params);
        Integer pageSize = PageUtils.getPageSize(params);
        Long userId = PageUtils.getLong(params, "userId");
        
        Page<UserLike> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<UserLike> wrapper = new LambdaQueryWrapper<>();
        
        if (userId != null) {
            wrapper.eq(UserLike::getUserId, userId);
        }
        
        // ✅ 只查询有效的点赞记录
        wrapper.eq(UserLike::getTargetType, "content")
               .eq(UserLike::getStatus, 1)
               .eq(UserLike::getDeleted, 0)
               .orderByDesc(UserLike::getLikeTime);
        
        IPage<UserLike> result = userLikeMapper.selectPage(page, wrapper);
        
        // 组装返回数据
        List<Map<String, Object>> list = new ArrayList<>();
        for (UserLike like : result.getRecords()) {
            // 获取内容详情
            Content content = contentMapper.selectById(like.getTargetId());
            
            // ✅ 过滤已删除的内容
            if (content == null || content.getDeleted() == 1) {
                continue;
            }
            
            Map<String, Object> item = new HashMap<>();
            item.put("id", like.getId());
            item.put("likeTime", like.getLikeTime());
            
            // 内容信息
            item.put("contentId", content.getId());
            item.put("title", content.getTitle());
            item.put("content", content.getContent());
            item.put("contentType", content.getContentType());
            item.put("contentSource", content.getContentSource());
            item.put("coverImage", content.getCoverImage());
            item.put("images", content.getImages());
            item.put("videoUrl", content.getVideoUrl());
            
            // 作者信息
            item.put("authorId", content.getAuthorId());
            item.put("authorName", content.getAuthorName());
            item.put("authorAvatar", content.getAuthorAvatar());
            
            // 互动数据
            item.put("views", content.getViews());
            item.put("likes", content.getLikes());
            item.put("comments", content.getComments());
            item.put("shares", content.getShares());
            item.put("collects", content.getCollects());
            
            // 时间
            item.put("createTime", content.getCreateTime());
            
            list.add(item);
        }
        
        return PageResult.of(result.getTotal(), list);
    }
    
    /**
     * 获取我的收藏（优化版）
     */
    public PageResult<Map<String, Object>> getMyCollects(Map<String, Object> params) {
        Integer pageNum = PageUtils.getPageNum(params);
        Integer pageSize = PageUtils.getPageSize(params);
        Long userId = PageUtils.getLong(params, "userId");
        
        Page<UserCollect> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<UserCollect> wrapper = new LambdaQueryWrapper<>();
        
        if (userId != null) {
            wrapper.eq(UserCollect::getUserId, userId);
        }
        
        // ✅ 只查询有效的收藏记录
        wrapper.eq(UserCollect::getTargetType, "content")
               .eq(UserCollect::getStatus, 1)
               .eq(UserCollect::getDeleted, 0)
               .orderByDesc(UserCollect::getCollectTime);
        
        IPage<UserCollect> result = userCollectMapper.selectPage(page, wrapper);
        
        // 组装返回数据
        List<Map<String, Object>> list = new ArrayList<>();
        for (UserCollect collect : result.getRecords()) {
            // 获取内容详情
            Content content = contentMapper.selectById(collect.getTargetId());
            
            // ✅ 过滤已删除的内容
            if (content == null || content.getDeleted() == 1) {
                continue;
            }
            
            Map<String, Object> item = new HashMap<>();
            item.put("id", collect.getId());
            item.put("collectTime", collect.getCollectTime());
            item.put("folderName", collect.getFolderName());
            
            // 内容信息
            item.put("contentId", content.getId());
            item.put("title", content.getTitle());
            item.put("content", content.getContent());
            item.put("contentType", content.getContentType());
            item.put("contentSource", content.getContentSource());
            item.put("coverImage", content.getCoverImage());
            item.put("images", content.getImages());
            item.put("videoUrl", content.getVideoUrl());
            
            // 作者信息
            item.put("authorId", content.getAuthorId());
            item.put("authorName", content.getAuthorName());
            item.put("authorAvatar", content.getAuthorAvatar());
            
            // 互动数据
            item.put("views", content.getViews());
            item.put("likes", content.getLikes());
            item.put("comments", content.getComments());
            item.put("shares", content.getShares());
            item.put("collects", content.getCollects());
            
            // 时间
            item.put("createTime", content.getCreateTime());
            
            list.add(item);
        }
        
        return PageResult.of(result.getTotal(), list);
    }
    
    /**
     * 取消收藏
     */
    public boolean cancelCollect(Long id) {
        return userCollectMapper.deleteById(id) > 0;
    }
    
    /**
     * 点赞内容
     */
    public boolean likeContent(Long userId, String username, Long contentId) {
        // 检查是否已经点赞
        LambdaQueryWrapper<UserLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserLike::getUserId, userId)
               .eq(UserLike::getTargetType, "content")
               .eq(UserLike::getTargetId, contentId)
               .eq(UserLike::getStatus, 1);
        
        UserLike existLike = userLikeMapper.selectOne(wrapper);
        if (existLike != null) {
            // 已经点赞，取消点赞
            existLike.setStatus(0);
            existLike.setCancelTime(java.time.LocalDateTime.now());
            userLikeMapper.updateById(existLike);
            
            // 更新 content 表的点赞数（减1）
            Content content = contentMapper.selectById(contentId);
            if (content != null && content.getLikes() > 0) {
                content.setLikes(content.getLikes() - 1);
                contentMapper.updateById(content);
            }
            
            return false; // 返回 false 表示取消点赞
        } else {
            // 未点赞，添加点赞记录
            Content content = contentMapper.selectById(contentId);
            if (content == null) {
                throw new RuntimeException("内容不存在");
            }
            
            UserLike like = new UserLike();
            like.setUserId(userId);
            like.setUserName(username);
            like.setTargetType("content");
            like.setTargetId(contentId);
            like.setTargetTitle(content.getTitle());
            like.setTargetAuthor(content.getAuthorName());
            like.setStatus(1);
            like.setLikeTime(java.time.LocalDateTime.now());
            
            userLikeMapper.insert(like);
            
            // 更新 content 表的点赞数（加1）
            content.setLikes(content.getLikes() + 1);
            contentMapper.updateById(content);
            
            return true; // 返回 true 表示点赞成功
        }
    }
    
    /**
     * 收藏内容
     */
    public boolean collectContent(Long userId, String username, Long contentId) {
        // 检查是否已经收藏
        LambdaQueryWrapper<UserCollect> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserCollect::getUserId, userId)
               .eq(UserCollect::getTargetType, "content")
               .eq(UserCollect::getTargetId, contentId)
               .eq(UserCollect::getStatus, 1);
        
        UserCollect existCollect = userCollectMapper.selectOne(wrapper);
        if (existCollect != null) {
            // 已经收藏，取消收藏
            existCollect.setStatus(0);
            userCollectMapper.updateById(existCollect);
            
            // 更新 content 表的收藏数（减1）
            Content content = contentMapper.selectById(contentId);
            if (content != null && content.getCollects() > 0) {
                content.setCollects(content.getCollects() - 1);
                contentMapper.updateById(content);
            }
            
            return false; // 返回 false 表示取消收藏
        } else {
            // 未收藏，添加收藏记录
            Content content = contentMapper.selectById(contentId);
            if (content == null) {
                throw new RuntimeException("内容不存在");
            }
            
            UserCollect collect = new UserCollect();
            collect.setUserId(userId);
            collect.setUserName(username);
            collect.setTargetType("content");
            collect.setTargetId(contentId);
            collect.setTargetTitle(content.getTitle());
            collect.setTargetAuthor(content.getAuthorName());
            collect.setFolderName("默认收藏夹");
            collect.setStatus(1);
            collect.setCollectTime(java.time.LocalDateTime.now());
            
            userCollectMapper.insert(collect);
            
            // 更新 content 表的收藏数（加1）
            content.setCollects(content.getCollects() + 1);
            contentMapper.updateById(content);
            
            return true; // 返回 true 表示收藏成功
        }
    }
    
    /**
     * 检查用户是否已点赞
     */
    public boolean checkUserLiked(Long userId, Long contentId) {
        if (userId == null) {
            return false;
        }
        
        LambdaQueryWrapper<UserLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserLike::getUserId, userId)
               .eq(UserLike::getTargetType, "content")
               .eq(UserLike::getTargetId, contentId)
               .eq(UserLike::getStatus, 1);
        
        return userLikeMapper.selectCount(wrapper) > 0;
    }
    
    /**
     * 检查用户是否已收藏
     */
    public boolean checkUserCollected(Long userId, Long contentId) {
        if (userId == null) {
            return false;
        }
        
        LambdaQueryWrapper<UserCollect> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserCollect::getUserId, userId)
               .eq(UserCollect::getTargetType, "content")
               .eq(UserCollect::getTargetId, contentId)
               .eq(UserCollect::getStatus, 1);
        
        return userCollectMapper.selectCount(wrapper) > 0;
    }
}
