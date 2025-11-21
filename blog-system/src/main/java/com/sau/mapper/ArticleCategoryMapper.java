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
    void add(List<ArticleCategory> categories);

    /**
     * 批量删除文章分类关联
     */
    void delete(@Param("ids") List<Integer> ids);
}
