package com.sau.mapper;

import com.sau.pojo.entity.ArticleCatecory;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 文章分类关联表
 */
@Mapper
public interface ArticleCatecoryMapper {
    /**
     * 新增文章分类关联
     */
    void add(List<ArticleCatecory> categories);
}
