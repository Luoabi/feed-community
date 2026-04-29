package org.xingchang.brapi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xingchang.brapi.entity.Content;
import org.xingchang.brapi.entity.ContentTag;
import org.xingchang.brapi.entity.Tag;
import org.xingchang.brapi.mapper.ContentMapper;
import org.xingchang.brapi.mapper.ContentTagMapper;
import org.xingchang.brapi.mapper.TagMapper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 数据初始化服务
 */
@Service
public class DataInitService {
    
    @Autowired
    private ContentMapper contentMapper;
    
    @Autowired
    private TagMapper tagMapper;
    
    @Autowired
    private ContentTagMapper contentTagMapper;
    
    @Autowired
    private ContentHotScoreService hotScoreService;
    
    /**
     * 初始化内容标签关联
     * 从content表的tags字段解析并插入到content_tag表
     */
    public int initContentTags() {
        System.out.println("开始初始化内容标签关联...");
        
        // 获取所有有标签的内容
        List<Content> contents = contentMapper.selectList(
            new LambdaQueryWrapper<Content>()
                .isNotNull(Content::getTags)
                .ne(Content::getTags, "")
        );
        
        int totalCount = 0;
        for (Content content : contents) {
            String[] tags = content.getTags().split(",");
            
            for (String tagName : tags) {
                tagName = tagName.trim();
                if (tagName.isEmpty()) {
                    continue;
                }
                
                // 查找或创建标签
                Tag tag = tagMapper.selectOne(
                    new LambdaQueryWrapper<Tag>()
                        .eq(Tag::getTagName, tagName)
                );
                
                if (tag == null) {
                    // 创建新标签
                    tag = new Tag();
                    tag.setTagName(tagName);
                    tag.setTagKey(tagName.toLowerCase().replaceAll("\\s+", "-"));
                    tag.setSort(0);
                    tag.setUserCount(0);
                    tag.setStatus(1);
                    tag.setDeleted(0);
                    tagMapper.insert(tag);
                }
                
                // 检查是否已存在关联
                Long existCount = contentTagMapper.selectCount(
                    new LambdaQueryWrapper<ContentTag>()
                        .eq(ContentTag::getContentId, content.getId())
                        .eq(ContentTag::getTagId, tag.getId())
                );
                
                if (existCount == 0) {
                    // 创建内容标签关联
                    ContentTag contentTag = new ContentTag();
                    contentTag.setContentId(content.getId());
                    contentTag.setTagId(tag.getId());
                    contentTag.setTagName(tag.getTagName());
                    contentTag.setWeight(1.0);
                    contentTag.setCreateTime(LocalDateTime.now());
                    contentTagMapper.insert(contentTag);
                    totalCount++;
                }
            }
        }
        
        System.out.println("内容标签关联初始化完成，共创建 " + totalCount + " 条关联");
        return totalCount;
    }
    
    /**
     * 初始化所有内容的热度分数
     */
    public int initHotScores() {
        System.out.println("开始初始化内容热度分数...");
        hotScoreService.updateAllHotScores();
        return 1;
    }
    
    /**
     * 完整初始化（标签关联 + 热度分数）
     */
    public void fullInit() {
        initContentTags();
        initHotScores();
    }
}
