package com.sau.mapper;

import com.sau.pojo.entity.ArticleCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文章分类关联表
 */
@Mapper
public interface ArticleCategoryMapper {
    /**
     * 新增文章分类关联
     */
    void save(List<ArticleCategory> categories);

    /**
     * 根据文章ID批量删除文章分类关联
     */
    void deleteByArticleIds(@Param("ids") List<Integer> ids);

    /**
     * 根据分类ID批量删除文章分类关联
     */
    void deleteByCategoryIds(@Param("ids") List<Integer> ids);
}
