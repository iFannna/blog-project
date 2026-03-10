package com.sau.service;

import com.sau.pojo.entity.Tag;

import java.util.List;

/**
 * 标签服务接口
 */
public interface TagService {

    /**
     * 创建标签
     */
    void create(Tag tag);

    /**
     * 删除标签
     */
    void deleteById(Integer id);

    /**
     * 查询全部标签
     */
    List<Tag> list();
}
