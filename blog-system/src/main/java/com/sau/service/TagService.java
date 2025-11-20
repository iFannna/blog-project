package com.sau.service;

import com.sau.pojo.entity.Tag;

import java.util.List;

public interface TagService {
    /**
     * 新增标签
     */
    void add(Tag tag);

    /**
     * 根据id删除标签
     */
    void delete(Integer id);

    /**
     * 查询所有标签
     */
    List<Tag> list();
}
