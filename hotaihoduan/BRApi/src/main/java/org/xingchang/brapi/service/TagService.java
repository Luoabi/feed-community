package org.xingchang.brapi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.xingchang.brapi.entity.Tag;
import org.xingchang.brapi.mapper.TagMapper;

import java.util.List;

/**
 * 标签Service
 */
@Service
public class TagService extends ServiceImpl<TagMapper, Tag> {
    
    public List<Tag> getList(String tagName, Integer status) {
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(tagName)) {
            wrapper.like(Tag::getTagName, tagName);
        }
        if (status != null) {
            wrapper.eq(Tag::getStatus, status);
        }
        
        wrapper.orderByAsc(Tag::getSort);
        
        return list(wrapper);
    }
    
    public boolean updateStatus(Long id, Integer status) {
        Tag tag = new Tag();
        tag.setId(id);
        tag.setStatus(status);
        return updateById(tag);
    }
}
