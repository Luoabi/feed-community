package org.xingchang.brapi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.xingchang.brapi.common.PageResult;
import org.xingchang.brapi.entity.Comment;
import org.xingchang.brapi.entity.Content;
import org.xingchang.brapi.entity.UserLike;
import org.xingchang.brapi.mapper.CommentMapper;
import org.xingchang.brapi.mapper.ContentMapper;
import org.xingchang.brapi.mapper.UserLikeMapper;
import org.xingchang.brapi.util.PageUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 评论Service
 */
@Service
public class CommentService extends ServiceImpl<CommentMapper, Comment> {
    
    @Autowired
    private ContentMapper contentMapper;
    
    @Autowired
    private UserLikeMapper userLikeMapper;
    
    /**
     * 获取评论列表（支持一级评论和二级回复）
     */
    public PageResult<Comment> getList(Map<String, Object> params) {
        Integer pageNum = PageUtils.getPageNum(params);
        Integer pageSize = PageUtils.getPageSize(params);
        String contentType = (String) params.get("contentType");
        Long contentId = params.get("contentId") != null ? Long.valueOf(params.get("contentId").toString()) : null;
        Long parentId = params.get("parentId") != null ? Long.valueOf(params.get("parentId").toString()) : null;
        Integer status = params.get("status") != null && !params.get("status").toString().isEmpty() 
            ? Integer.valueOf(params.get("status").toString()) : null;
        
        Page<Comment> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(contentType)) {
            wrapper.eq(Comment::getContentType, contentType);
        }
        if (contentId != null) {
            wrapper.eq(Comment::getContentId, contentId);
        }
        if (parentId != null) {
            wrapper.eq(Comment::getParentId, parentId);
        } else {
            // 如果没有指定 parentId，默认只查询一级评论
            wrapper.eq(Comment::getParentId, 0);
        }
        if (status != null) {
            wrapper.eq(Comment::getStatus, status);
        }
        
        // 置顶的评论排在前面，然后按创建时间倒序
        wrapper.orderByDesc(Comment::getIsTop)
               .orderByDesc(Comment::getCreateTime);
        
        IPage<Comment> result = page(page, wrapper);
        return PageResult.of(result.getTotal(), result.getRecords());
    }
    
    /**
     * 获取评论树（一级评论 + 二级回复）
     */
    public PageResult<Map<String, Object>> getCommentTree(Map<String, Object> params) {
        Integer pageNum = PageUtils.getPageNum(params);
        Integer pageSize = PageUtils.getPageSize(params);
        String contentType = (String) params.get("contentType");
        Long contentId = params.get("contentId") != null ? Long.valueOf(params.get("contentId").toString()) : null;
        
        // 1. 查询一级评论（分页）
        Page<Comment> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(contentType)) {
            wrapper.eq(Comment::getContentType, contentType);
        }
        if (contentId != null) {
            wrapper.eq(Comment::getContentId, contentId);
        }
        
        wrapper.eq(Comment::getParentId, 0)
               .eq(Comment::getStatus, 1)  // 只显示正常状态的评论
               .orderByDesc(Comment::getIsTop)
               .orderByDesc(Comment::getCreateTime);
        
        IPage<Comment> result = page(page, wrapper);
        List<Comment> rootComments = result.getRecords();
        
        // 2. 查询这些一级评论的所有二级回复
        List<Map<String, Object>> commentTree = new ArrayList<>();
        for (Comment rootComment : rootComments) {
            Map<String, Object> item = new HashMap<>();
            item.put("comment", rootComment);
            
            // ✅ 修复：查询该评论的回复（最多显示3条，更多的需要点击"查看更多"）
            // 必须同时过滤 contentId，防止显示其他文章的评论
            LambdaQueryWrapper<Comment> replyWrapper = new LambdaQueryWrapper<>();
            replyWrapper.eq(Comment::getContentId, rootComment.getContentId())  // ✅ 添加 contentId 过滤
                       .eq(Comment::getParentId, rootComment.getId())
                       .eq(Comment::getStatus, 1)
                       .orderByAsc(Comment::getCreateTime)
                       .last("LIMIT 3");
            
            List<Comment> replies = list(replyWrapper);
            item.put("replies", replies);
            item.put("hasMore", rootComment.getReplyCount() > 3);
            
            commentTree.add(item);
        }
        
        return PageResult.of(result.getTotal(), commentTree);
    }
    
    /**
     * 获取某条评论的所有回复
     */
    public PageResult<Comment> getReplies(Long parentId, Map<String, Object> params) {
        Integer pageNum = PageUtils.getPageNum(params);
        Integer pageSize = PageUtils.getPageSize(params);
        
        // ✅ 修复：获取父评论，确保回复属于同一篇文章
        Comment parentComment = getById(parentId);
        if (parentComment == null) {
            return PageResult.of(0L, new ArrayList<>());
        }
        
        Page<Comment> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        
        wrapper.eq(Comment::getContentId, parentComment.getContentId())  // ✅ 添加 contentId 过滤
               .eq(Comment::getParentId, parentId)
               .eq(Comment::getStatus, 1)
               .orderByAsc(Comment::getCreateTime);
        
        IPage<Comment> result = page(page, wrapper);
        return PageResult.of(result.getTotal(), result.getRecords());
    }
    
    /**
     * 添加评论
     */
    public boolean addComment(Comment comment) {
        // 设置默认值
        if (comment.getParentId() == null) {
            comment.setParentId(0L);
        }
        if (comment.getRootId() == null) {
            comment.setRootId(0L);
        }
        if (comment.getLikes() == null) {
            comment.setLikes(0);
        }
        if (comment.getReplyCount() == null) {
            comment.setReplyCount(0);
        }
        if (comment.getStatus() == null) {
            comment.setStatus(1);  // 默认正常状态（如需审核，改为2）
        }
        if (comment.getIsTop() == null) {
            comment.setIsTop(0);
        }
        
        // 获取内容标题
        if ("content".equals(comment.getContentType())) {
            Content content = contentMapper.selectById(comment.getContentId());
            if (content != null) {
                comment.setContentTitle(content.getTitle());
            }
        }
        
        // 如果是回复评论，需要设置 rootId 并更新回复数
        if (comment.getParentId() > 0) {
            Comment parentComment = getById(comment.getParentId());
            if (parentComment != null) {
                // 如果父评论是一级评论，则 rootId 就是父评论的 id
                // 如果父评论是二级评论，则 rootId 继承父评论的 rootId
                comment.setRootId(parentComment.getRootId() > 0 ? parentComment.getRootId() : parentComment.getId());
                
                // ✅ 修复：始终更新一级评论（根评论）的回复数
                Long rootCommentId = comment.getRootId();
                Comment rootComment = getById(rootCommentId);
                if (rootComment != null) {
                    rootComment.setReplyCount(rootComment.getReplyCount() + 1);
                    updateById(rootComment);
                }
            }
        }
        
        // 保存评论
        boolean success = save(comment);
        
        // 更新 content 表的评论数
        if (success && "content".equals(comment.getContentType())) {
            Content content = contentMapper.selectById(comment.getContentId());
            if (content != null) {
                content.setComments(content.getComments() + 1);
                contentMapper.updateById(content);
            }
        }
        
        return success;
    }
    
    /**
     * 删除评论
     */
    public boolean deleteComment(Long id) {
        Comment comment = getById(id);
        if (comment == null) {
            return false;
        }
        
        // 删除评论
        boolean success = removeById(id);
        
        if (success) {
            // ✅ 修复：如果是回复，更新一级评论（根评论）的回复数
            if (comment.getParentId() > 0) {
                Long rootCommentId = comment.getRootId();
                Comment rootComment = getById(rootCommentId);
                if (rootComment != null && rootComment.getReplyCount() > 0) {
                    rootComment.setReplyCount(rootComment.getReplyCount() - 1);
                    updateById(rootComment);
                }
            }
            
            // 更新 content 表的评论数
            if ("content".equals(comment.getContentType())) {
                Content content = contentMapper.selectById(comment.getContentId());
                if (content != null && content.getComments() > 0) {
                    content.setComments(content.getComments() - 1);
                    contentMapper.updateById(content);
                }
            }
            
            // 如果是一级评论，删除其所有回复
            if (comment.getParentId() == 0) {
                LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(Comment::getContentId, comment.getContentId())  // ✅ 添加 contentId 过滤
                       .eq(Comment::getParentId, id);
                List<Comment> replies = list(wrapper);
                if (!replies.isEmpty()) {
                    List<Long> replyIds = replies.stream().map(Comment::getId).collect(Collectors.toList());
                    removeByIds(replyIds);
                    
                    // 更新 content 表的评论数（减去回复数）
                    if ("content".equals(comment.getContentType())) {
                        Content content = contentMapper.selectById(comment.getContentId());
                        if (content != null) {
                            content.setComments(Math.max(0, content.getComments() - replies.size()));
                            contentMapper.updateById(content);
                        }
                    }
                }
            }
        }
        
        return success;
    }
    
    /**
     * 点赞评论
     */
    public boolean likeComment(Long userId, String username, Long commentId) {
        // 检查是否已经点赞
        LambdaQueryWrapper<UserLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserLike::getUserId, userId)
               .eq(UserLike::getTargetType, "comment")
               .eq(UserLike::getTargetId, commentId)
               .eq(UserLike::getStatus, 1);
        
        UserLike existLike = userLikeMapper.selectOne(wrapper);
        if (existLike != null) {
            // 已经点赞，取消点赞
            existLike.setStatus(0);
            existLike.setCancelTime(LocalDateTime.now());
            userLikeMapper.updateById(existLike);
            
            // 更新评论的点赞数（减1）
            Comment comment = getById(commentId);
            if (comment != null && comment.getLikes() > 0) {
                comment.setLikes(comment.getLikes() - 1);
                updateById(comment);
            }
            
            return false; // 返回 false 表示取消点赞
        } else {
            // 未点赞，添加点赞记录
            Comment comment = getById(commentId);
            if (comment == null) {
                throw new RuntimeException("评论不存在");
            }
            
            UserLike like = new UserLike();
            like.setUserId(userId);
            like.setUserName(username);
            like.setTargetType("comment");
            like.setTargetId(commentId);
            like.setTargetTitle(comment.getContent().substring(0, Math.min(50, comment.getContent().length())));
            like.setTargetAuthor(comment.getUserName());
            like.setStatus(1);
            like.setLikeTime(LocalDateTime.now());
            
            userLikeMapper.insert(like);
            
            // 更新评论的点赞数（加1）
            comment.setLikes(comment.getLikes() + 1);
            updateById(comment);
            
            return true; // 返回 true 表示点赞成功
        }
    }
    
    /**
     * 检查用户是否已点赞评论
     */
    public boolean checkUserLiked(Long userId, Long commentId) {
        if (userId == null) {
            return false;
        }
        
        LambdaQueryWrapper<UserLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserLike::getUserId, userId)
               .eq(UserLike::getTargetType, "comment")
               .eq(UserLike::getTargetId, commentId)
               .eq(UserLike::getStatus, 1);
        
        return userLikeMapper.selectCount(wrapper) > 0;
    }
    
    /**
     * 置顶/取消置顶
     */
    public boolean toggleTop(Long id, Integer isTop) {
        Comment comment = new Comment();
        comment.setId(id);
        comment.setIsTop(isTop);
        return updateById(comment);
    }
    
    /**
     * 修改状态（审核）
     */
    public boolean updateStatus(Long id, Integer status) {
        Comment comment = new Comment();
        comment.setId(id);
        comment.setStatus(status);
        return updateById(comment);
    }
}
