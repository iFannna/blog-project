package com.sau.mapper;

import com.sau.pojo.entity.Tag;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 标签数据访问层
 */
@Mapper
public interface TagMapper {
    /**
     * 插入标签
     */
    @Insert("insert into tag(name) values(#{name})")
    void insert(Tag tag);

    /**
     * 根据 ID 删除标签
     */
    @Delete("delete from tag where id = #{id}")
    void deleteById(Integer id);

    /**
     * 查询所有标签
     */
    @Select("select id, name, create_time, update_time from tag")
    List<Tag> list();
}
