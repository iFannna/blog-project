package com.sau.mapper;

import com.sau.pojo.entity.ArticleTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文章标签关联数据访问层
 */
@Mapper
public interface ArticleTagMapper {
    /**
     * 批量插入文章标签关联关系
     */
    void insertBatch(List<ArticleTag> tags);

    /**
     * 根据文章 ID 批量删除文章标签关联关系
     */
    void deleteByArticleIds(@Param("ids") List<Integer> ids);

    /**
     * 根据标签 ID 批量删除文章标签关联关系
     */
    void deleteByTagIds(@Param("ids") List<Integer> ids);
}
