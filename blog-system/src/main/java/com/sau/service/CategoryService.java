package com.sau.service;

import com.sau.pojo.entity.Category;

import java.util.List;

/**
 * 分类服务接口。
 */
public interface CategoryService {

    /**
     * 查询全部分类。
     */
    List<Category> list();

    /**
     * 创建分类。
     */
    void create(Category category);

    /**
     * 删除分类。
     */
    void deleteById(Integer id);
}