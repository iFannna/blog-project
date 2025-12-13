package com.sau.mapper;

import com.sau.pojo.entity.Tag;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 标签表
 */
@Mapper
public interface TagMapper {
    /**
     * 新增标签
     */
    @Insert("insert into tag(name) values(#{name})")
    void save(Tag tag);

    /**
     * 根据ID删除标签
     */
    @Delete("delete from tag where id = #{id}")
    void deleteById(Integer id);

    /**
     * 查询所有标签
     */
    @Select("select id, name, create_time, update_time from tag")
    List<Tag> list();
}
