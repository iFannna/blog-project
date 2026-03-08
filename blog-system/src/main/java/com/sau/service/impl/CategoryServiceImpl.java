package com.sau.service.impl;

import com.sau.mapper.ArticleCategoryMapper;
import com.sau.mapper.CategoryMapper;
import com.sau.pojo.entity.Category;
import com.sau.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 分类服务实现类。
 */
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;
    private final ArticleCategoryMapper articleCategoryMapper;

    /**
     * 查询全部分类。
     */
    @Override
    public List<Category> list() {
        return categoryMapper.list();
    }

    /**
     * 创建分类。
     */
    @Override
    public void create(Category category) {
        categoryMapper.insert(category);
    }

    /**
     * 删除分类并清理文章分类关系。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteById(Integer id) {
        categoryMapper.deleteById(id);
        articleCategoryMapper.deleteByCategoryIds(List.of(id));
    }
}