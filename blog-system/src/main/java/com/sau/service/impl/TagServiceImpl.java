package com.sau.service.impl;

import com.sau.mapper.ArticleTagMapper;
import com.sau.mapper.TagMapper;
import com.sau.pojo.entity.Tag;
import com.sau.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 标签服务实现类。
 */
@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagMapper tagMapper;
    private final ArticleTagMapper articleTagMapper;

    /**
     * 创建标签。
     */
    @Override
    public void create(Tag tag) {
        tagMapper.insert(tag);
    }

    /**
     * 删除标签并清理文章标签关系。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteById(Integer id) {
        tagMapper.deleteById(id);
        articleTagMapper.deleteByTagIds(List.of(id));
    }

    /**
     * 查询全部标签。
     */
    @Override
    public List<Tag> list() {
        return tagMapper.list();
    }
}