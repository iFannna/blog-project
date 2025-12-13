package com.sau.service.impl;

import com.sau.mapper.ArticleTagMapper;
import com.sau.mapper.TagMapper;
import com.sau.pojo.entity.Tag;
import com.sau.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {
    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private ArticleTagMapper articleTagMapper;
    /**
     * 新增标签
     */
    @Override
    public void save(Tag tag) {
        tagMapper.save(tag);
    }

    /**
     * 根据id删除标签
     */
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void delete(Integer id) {
        // 删除标签
        tagMapper.deleteById(id);
        // 删除标签和文章的关联关系
        articleTagMapper.deleteByTagIds(List.of(id));
    }

    /**
     * 查询所有标签
     */
    @Override
    public List<Tag> list() {
        return tagMapper.list();
    }
}
