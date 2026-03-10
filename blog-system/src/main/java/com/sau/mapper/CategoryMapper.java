package com.sau.mapper;

import com.sau.pojo.entity.Category;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CategoryMapper {
    /**
     * 查询所有分类
     */
    @Select("select id, name, create_time, update_time from category")
    List<Category> list();

    /**
     * 插入分类
     */
    @Insert("insert into category(name) values(#{name})")
    void insert(Category category);

    /**
     * 根据 ID 删除分类
     */
    @Delete("delete from category where id = #{id}")
    void deleteById(Integer id);
}
