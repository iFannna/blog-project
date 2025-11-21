package com.sau.service.impl;

import com.sau.mapper.CategoryMapper;
import com.sau.pojo.entity.Category;
import com.sau.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 查询所有分类
     */
    @Override
    public List<Category> list() {
        return categoryMapper.list();
    }

    /**
     * 新增分类
     */
    @Override
    public void add(Category category) {
        categoryMapper.add(category);
    }

    /**
     * 根据ID删除分类
     */
    @Override
    public void delete(Integer id) {
        categoryMapper.deleteById(id);
    }
}
