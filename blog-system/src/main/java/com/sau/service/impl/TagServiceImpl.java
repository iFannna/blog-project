package com.sau.service.impl;

import com.sau.mapper.TagMapper;
import com.sau.pojo.entity.Tag;
import com.sau.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TagServiceImpl implements TagService {
    @Autowired
    private TagMapper tagMapper;
    /**
     * 新增标签
     */
    @Override
    public void add(Tag tag) {
        // 设置标签创建时间和修改时间
        tag.setCreateTime(LocalDateTime.now());
        tag.setUpdateTime(LocalDateTime.now());
        // 插入标签
        tagMapper.add(tag);
    }

    /**
     * 根据id删除标签
     */
    @Override
    public void delete(Integer id) {
        tagMapper.deleteById(id);
    }

    /**
     * 查询所有标签
     */
    @Override
    public List<Tag> list() {
        return tagMapper.list();
    }
}
