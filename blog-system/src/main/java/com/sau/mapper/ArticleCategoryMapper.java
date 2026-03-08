package com.sau.mapper;

import com.sau.pojo.entity.ArticleCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文章分类关联数据访问层。
 */
@Mapper
public interface ArticleCategoryMapper {
    /**
     * 批量插入文章分类关联关系。
     */
    void insertBatch(List<ArticleCategory> categories);

    /**
     * 根据文章 ID 批量删除文章分类关联关系。
     */
    void deleteByArticleIds(@Param("ids") List<Integer> ids);

    /**
     * 根据分类 ID 批量删除文章分类关联关系。
     */
    void deleteByCategoryIds(@Param("ids") List<Integer> ids);
}