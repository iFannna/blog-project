package com.sau.mapper;

import com.sau.pojo.entity.ArticleTag;
import com.sau.pojo.entity.Tag;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 文章标签关联表
 */
@Mapper
public interface ArticleTagMapper {
    /**
     * 新增文章标签关联
     */
    void add(List<ArticleTag> tags);
}
