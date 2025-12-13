package com.sau.mapper;

import com.sau.pojo.entity.ArticleTag;
import com.sau.pojo.entity.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文章标签关联表
 */
@Mapper
public interface ArticleTagMapper {
    /**
     * 新增文章标签关联
     */
    void save(List<ArticleTag> tags);

    /**
     * 根据文章ID批量删除文章标签关联
     */
    void deleteByArticleIds(@Param("ids") List<Integer> ids);

    /**
     * 根据标签ID批量删除文章标签关联
     */
    void deleteByTagIds(@Param("ids") List<Integer> ids);
}
