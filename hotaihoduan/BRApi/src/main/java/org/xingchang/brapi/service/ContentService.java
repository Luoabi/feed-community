package org.xingchang.brapi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.xingchang.brapi.common.PageResult;
import org.xingchang.brapi.entity.Content;
import org.xingchang.brapi.entity.UserLike;
import org.xingchang.brapi.entity.UserCollect;
import org.xingchang.brapi.entity.Comment;
import org.xingchang.brapi.mapper.ContentMapper;
import org.xingchang.brapi.mapper.UserLikeMapper;
import org.xingchang.brapi.mapper.UserCollectMapper;
import org.xingchang.brapi.mapper.CommentMapper;
import org.xingchang.brapi.util.PageUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ContentService extends ServiceImpl<ContentMapper, Content> {
    
    @Autowired
    private UserLikeMapper userLikeMapper;
    
    @Autowired
    private UserCollectMapper userCollectMapper;
    
    @Autowired
    private CommentMapper commentMapper;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    public PageResult<Content> getList(Map<String, Object> params) {
        // ✅ 使用 PageUtils 安全地获取分页参数
        Integer pageNum = PageUtils.getPageNum(params);
        Integer pageSize = PageUtils.getPageSize(params);
        String title = PageUtils.getString(params, "title");
        String status = PageUtils.getString(params, "status");
        String contentType = PageUtils.getString(params, "contentType");
        String contentSource = PageUtils.getString(params, "contentSource");
        Long categoryId = PageUtils.getLong(params, "categoryId");
        Long authorId = PageUtils.getLong(params, "authorId");
        
        // ✅ 判断是否为Feed流推荐查询（首页）
        Boolean isFeed = PageUtils.getBoolean(params, "isFeed");
        
        Page<Content> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Content> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(title)) {
            wrapper.like(Content::getTitle, title);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(Content::getStatus, status);
        } else {
            // 默认只查已发布的内容
            wrapper.eq(Content::getStatus, "published");
        }
        if (StringUtils.hasText(contentType)) {
            wrapper.eq(Content::getContentType, contentType);
        }
        if (StringUtils.hasText(contentSource)) {
            wrapper.eq(Content::getContentSource, contentSource);
        }
        if (categoryId != null) {
            wrapper.eq(Content::getCategoryId, categoryId);
        }
        if (authorId != null) {
            wrapper.eq(Content::getAuthorId, authorId);
        }
        
        // ✅ Feed流推荐排序算法
        if (Boolean.TRUE.equals(isFeed)) {
            // 推荐算法：置顶 > 推荐 > 热门 > 精华 > 时间
            wrapper.orderByDesc(Content::getIsTop)        // 1. 置顶优先
                   .orderByDesc(Content::getIsRecommend)  // 2. 推荐内容
                   .orderByDesc(Content::getIsHot)        // 3. 热门内容
                   .orderByDesc(Content::getIsEssence)    // 4. 精华内容
                   .orderByDesc(Content::getViews)        // 5. 浏览量
                   .orderByDesc(Content::getLikes)        // 6. 点赞数
                   .orderByDesc(Content::getCreateTime);  // 7. 最新发布
        } else {
            // 普通列表：置顶 > 时间
            wrapper.orderByDesc(Content::getIsTop)
                   .orderByDesc(Content::getCreateTime);
        }
        
        IPage<Content> result = page(page, wrapper);
        
        // ✅ 手动处理 images 字段（因为 TypeHandler 不生效）
        List<Content> records = result.getRecords();
        for (Content content : records) {
            if (content.getImages() == null && content.getId() != null) {
                // 直接从数据库读取 images 字段的原始 JSON 字符串
                try {
                    String imagesJson = jdbcTemplate.queryForObject(
                        "SELECT images FROM content WHERE id = ?",
                        String.class,
                        content.getId()
                    );
                    
                    if (imagesJson != null && !imagesJson.trim().isEmpty()) {
                        // 解析 JSON 字符串为 List<String>
                        List<String> imagesList = objectMapper.readValue(
                            imagesJson, 
                            new TypeReference<List<String>>() {}
                        );
                        content.setImages(imagesList);
                        System.out.println("✅ 手动解析 images 成功 - ID: " + content.getId() + ", images: " + imagesList);
                    }
                } catch (Exception e) {
                    System.err.println("❌ 解析 images 失败 - ID: " + content.getId() + ", error: " + e.getMessage());
                    content.setImages(new ArrayList<>());
                }
            }
        }
        
        // ✅ 调试日志：检查 images 字段是否正确加载
        if (!records.isEmpty()) {
            // 查找 ID 为 19 的记录
            Content testContent = records.stream()
                .filter(c -> c.getId() == 19L)
                .findFirst()
                .orElse(records.get(0));
            
            System.out.println("=== Content查询结果调试 ===");
            System.out.println("Content ID: " + testContent.getId());
            System.out.println("Content Title: " + testContent.getTitle());
            System.out.println("Content Type: " + testContent.getContentType());
            System.out.println("Images字段值: " + testContent.getImages());
            System.out.println("Images是否为null: " + (testContent.getImages() == null));
            if (testContent.getImages() != null) {
                System.out.println("Images数量: " + testContent.getImages().size());
            }
        }
        
        return PageResult.of(result.getTotal(), records);
    }
    
    public boolean batchDelete(List<Long> ids) {
        return removeBatchByIds(ids);
    }
    
    public boolean toggleTop(Long id, Integer isTop) {
        Content content = new Content();
        content.setId(id);
        content.setIsTop(isTop);
        return updateById(content);
    }
    
    public boolean toggleEssence(Long id, Integer isEssence) {
        Content content = new Content();
        content.setId(id);
        content.setIsEssence(isEssence);
        return updateById(content);
    }
    
    public boolean toggleRecommend(Long id, Integer isRecommend) {
        Content content = new Content();
        content.setId(id);
        content.setIsRecommend(isRecommend);
        return updateById(content);
    }
    
    /**
     * 获取我的发布
     */
    public PageResult<Content> getMyPosts(Map<String, Object> params) {
        // ✅ 使用 PageUtils 安全地获取分页参数
        Integer pageNum = PageUtils.getPageNum(params);
        Integer pageSize = PageUtils.getPageSize(params);
        Long authorId = PageUtils.getLong(params, "authorId");
        String status = PageUtils.getString(params, "status");
        
        Page<Content> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Content> wrapper = new LambdaQueryWrapper<>();
        
        // ✅ 查询条件：用户发布的内容
        if (authorId != null) {
            wrapper.eq(Content::getAuthorId, authorId);
        }
        wrapper.eq(Content::getContentSource, "user")  // 只查询用户发布的
               .eq(Content::getDeleted, 0);             // 未删除的
        
        // 可选：按状态过滤
        if (StringUtils.hasText(status)) {
            wrapper.eq(Content::getStatus, status);
        }
        
        // 按创建时间倒序
        wrapper.orderByDesc(Content::getCreateTime);
        
        IPage<Content> result = page(page, wrapper);
        return PageResult.of(result.getTotal(), result.getRecords());
    }
    
    /**
     * 增加浏览数
     */
    public void incrementViews(Long id) {
        Content content = getById(id);
        if (content != null) {
            content.setViews(content.getViews() + 1);
            updateById(content);
        }
    }
    
    /**
     * 获取内容详情（手动处理 images 字段）
     */
    public Content getContentById(Long id) {
        Content content = getById(id);
        if (content != null && content.getImages() == null) {
            // 手动从数据库读取 images 字段
            try {
                String imagesJson = jdbcTemplate.queryForObject(
                    "SELECT images FROM content WHERE id = ?",
                    String.class,
                    id
                );
                
                if (imagesJson != null && !imagesJson.trim().isEmpty()) {
                    List<String> imagesList = objectMapper.readValue(
                        imagesJson, 
                        new TypeReference<List<String>>() {}
                    );
                    content.setImages(imagesList);
                    System.out.println("✅ 详情页手动解析 images 成功 - ID: " + id + ", images: " + imagesList);
                }
            } catch (Exception e) {
                System.err.println("❌ 详情页解析 images 失败 - ID: " + id + ", error: " + e.getMessage());
                content.setImages(new ArrayList<>());
            }
        }
        return content;
    }
    
    /**
     * 级联删除内容及相关数据
     * 删除内容时，同时删除：
     * 1. 该内容的所有点赞记录
     * 2. 该内容的所有收藏记录
     * 3. 该内容的所有评论
     */
    public boolean deleteContentWithRelations(Long contentId) {
        // 1. 删除点赞记录
        LambdaQueryWrapper<UserLike> likeWrapper = new LambdaQueryWrapper<>();
        likeWrapper.eq(UserLike::getTargetType, "content")
                   .eq(UserLike::getTargetId, contentId);
        userLikeMapper.delete(likeWrapper);
        
        // 2. 删除收藏记录
        LambdaQueryWrapper<UserCollect> collectWrapper = new LambdaQueryWrapper<>();
        collectWrapper.eq(UserCollect::getTargetType, "content")
                      .eq(UserCollect::getTargetId, contentId);
        userCollectMapper.delete(collectWrapper);
        
        // 3. 删除评论记录
        LambdaQueryWrapper<Comment> commentWrapper = new LambdaQueryWrapper<>();
        commentWrapper.eq(Comment::getContentType, "content")
                      .eq(Comment::getContentId, contentId);
        commentMapper.delete(commentWrapper);
        
        // 4. 删除内容本身
        return removeById(contentId);
    }
    
    /**
     * 通过审核
     */
    public boolean approveContent(Long id) {
        Content content = new Content();
        content.setId(id);
        content.setStatus("published");  // 状态改为已发布
        content.setPublishTime(java.time.LocalDateTime.now());
        return updateById(content);
    }
    
    /**
     * 拒绝审核
     */
    public boolean rejectContent(Long id, String reason, String violationType) {
        Content content = getById(id);
        if (content == null) {
            return false;
        }
        content.setStatus("rejected");  // 状态改为已拒绝
        // 注意：需要在 Content 实体中添加 rejectReason 和 violationType 字段
        // 或者创建单独的审核记录表
        return updateById(content);
    }
    
    /**
     * 批量通过审核
     */
    public boolean batchApprove(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        for (Long id : ids) {
            approveContent(id);
        }
        return true;
    }
    
    /**
     * 批量拒绝审核
     */
    public boolean batchReject(List<Long> ids, String reason, String violationType) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        for (Long id : ids) {
            rejectContent(id, reason, violationType);
        }
        return true;
    }
    
    /**
     * 审核统计
     */
    public Map<String, Object> getAuditStats() {
        // 待审核数量
        Long pendingCount = count(new LambdaQueryWrapper<Content>()
            .eq(Content::getStatus, "pending")
            .eq(Content::getDeleted, 0));
        
        // 已通过数量
        Long approvedCount = count(new LambdaQueryWrapper<Content>()
            .eq(Content::getStatus, "published")
            .eq(Content::getDeleted, 0));
        
        // 已拒绝数量
        Long rejectedCount = count(new LambdaQueryWrapper<Content>()
            .eq(Content::getStatus, "rejected")
            .eq(Content::getDeleted, 0));
        
        // 今日待审核
        java.time.LocalDateTime todayStart = java.time.LocalDateTime.of(
            java.time.LocalDate.now(), 
            java.time.LocalTime.MIN
        );
        Long todayPendingCount = count(new LambdaQueryWrapper<Content>()
            .eq(Content::getStatus, "pending")
            .ge(Content::getCreateTime, todayStart)
            .eq(Content::getDeleted, 0));
        
        return Map.of(
            "pendingCount", pendingCount,
            "approvedCount", approvedCount,
            "rejectedCount", rejectedCount,
            "todayPendingCount", todayPendingCount
        );
    }
    
    /**
     * 更新热度分数
     */
    public boolean updateHotScore(Long id, Double hotScore) {
        Content content = new Content();
        content.setId(id);
        content.setHotScore(hotScore);
        return updateById(content);
    }
    
    /**
     * 获取Feed流内容列表（后台管理）
     */
    public PageResult<Content> getFeedContentList(Map<String, Object> params) {
        // ✅ 使用 PageUtils 安全地获取分页参数
        Integer pageNum = PageUtils.getPageNum(params);
        Integer pageSize = PageUtils.getPageSize(params);
        String title = PageUtils.getString(params, "title");
        String contentType = PageUtils.getString(params, "contentType");
        
        Page<Content> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Content> wrapper = new LambdaQueryWrapper<>();
        
        // Feed流条件：已发布 + 已推荐
        wrapper.eq(Content::getStatus, "published")
               .eq(Content::getIsRecommend, 1)
               .eq(Content::getDeleted, 0);
        
        if (StringUtils.hasText(title)) {
            wrapper.like(Content::getTitle, title);
        }
        if (StringUtils.hasText(contentType)) {
            wrapper.eq(Content::getContentType, contentType);
        }
        
        // 排序：置顶优先，然后按热度分
        wrapper.orderByDesc(Content::getIsTop)
               .orderByDesc(Content::getHotScore);
        
        IPage<Content> result = page(page, wrapper);
        
        // ✅ 手动处理 images 字段
        List<Content> records = result.getRecords();
        for (Content content : records) {
            if (content.getImages() == null && content.getId() != null) {
                try {
                    String imagesJson = jdbcTemplate.queryForObject(
                        "SELECT images FROM content WHERE id = ?",
                        String.class,
                        content.getId()
                    );
                    
                    if (imagesJson != null && !imagesJson.trim().isEmpty()) {
                        List<String> imagesList = objectMapper.readValue(
                            imagesJson, 
                            new TypeReference<List<String>>() {}
                        );
                        content.setImages(imagesList);
                    }
                } catch (Exception e) {
                    content.setImages(new ArrayList<>());
                }
            }
        }
        
        return PageResult.of(result.getTotal(), records);
    }
    
    /**
     * 推送到Feed流
     */
    public boolean pushToFeed(Long contentId, Double initialScore) {
        Content content = getById(contentId);
        if (content == null) {
            return false;
        }
        content.setIsRecommend(1);
        if (initialScore != null) {
            content.setHotScore(initialScore);
        }
        return updateById(content);
    }
    
    /**
     * 从Feed流移除
     */
    public boolean removeFromFeed(Long contentId) {
        Content content = getById(contentId);
        if (content == null) {
            return false;
        }
        content.setIsRecommend(0);
        return updateById(content);
    }
    
    /**
     * 获取Feed流统计
     */
    public Map<String, Object> getFeedStats() {
        // Feed流内容总数
        Long totalCount = count(new LambdaQueryWrapper<Content>()
            .eq(Content::getIsRecommend, 1)
            .eq(Content::getDeleted, 0));
        
        // 今日新增Feed流内容
        java.time.LocalDateTime todayStart = java.time.LocalDateTime.of(
            java.time.LocalDate.now(), 
            java.time.LocalTime.MIN
        );
        Long todayPushCount = count(new LambdaQueryWrapper<Content>()
            .eq(Content::getIsRecommend, 1)
            .ge(Content::getCreateTime, todayStart)
            .eq(Content::getDeleted, 0));
        
        // 平均热度分数
        Double avgHotScore = 0.0;
        try {
            List<Content> allFeedContent = list(new LambdaQueryWrapper<Content>()
                .eq(Content::getIsRecommend, 1)
                .eq(Content::getDeleted, 0));
            if (!allFeedContent.isEmpty()) {
                double sum = allFeedContent.stream()
                    .mapToDouble(c -> c.getHotScore() != null ? c.getHotScore() : 0.0)
                    .sum();
                avgHotScore = sum / allFeedContent.size();
            }
        } catch (Exception e) {
            avgHotScore = 0.0;
        }
        
        return Map.of(
            "totalContent", totalCount,
            "todayPush", todayPushCount,
            "cacheSize", 1024,
            "avgScore", avgHotScore
        );
    }
}
