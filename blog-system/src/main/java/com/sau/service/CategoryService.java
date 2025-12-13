package com.sau.service;

import com.sau.pojo.entity.Category;

import java.util.List;

public interface CategoryService {
    /**
     * 查询所有分类
     */
    List<Category> list();

    /**
     * 新增分类
     */
    void save(Category category);

    /**
     * 根据ID删除分类
     */
    void delete(Integer id);
}
