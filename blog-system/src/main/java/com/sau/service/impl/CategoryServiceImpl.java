package com.sau.service.impl;

import com.sau.mapper.ArticleCategoryMapper;
import com.sau.mapper.CategoryMapper;
import com.sau.pojo.entity.Category;
import com.sau.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ArticleCategoryMapper articleCategoryMapper;

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
    public void save(Category category) {
        categoryMapper.save(category);
    }

    /**
     * 根据ID删除分类
     */
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void delete(Integer id) {
        // 删除分类
        categoryMapper.deleteById(id);
        // 删除分类和文章的关联关系
        articleCategoryMapper.deleteByCategoryIds(List.of(id));
    }
}
