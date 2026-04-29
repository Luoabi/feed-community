package org.xingchang.brapi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.xingchang.brapi.entity.UserFeed;

/**
 * 用户个性化Feed流Mapper
 */
@Mapper
public interface UserFeedMapper extends BaseMapper<UserFeed> {
}
