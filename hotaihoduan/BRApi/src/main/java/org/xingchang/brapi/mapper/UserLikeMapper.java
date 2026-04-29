package org.xingchang.brapi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.xingchang.brapi.entity.UserLike;

/**
 * 用户点赞Mapper
 */
@Mapper
public interface UserLikeMapper extends BaseMapper<UserLike> {
}
