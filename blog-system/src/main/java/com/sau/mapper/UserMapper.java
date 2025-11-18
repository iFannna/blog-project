package com.sau.mapper;

import com.sau.pojo.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    /**
     * 查询用户
     */
    @Select("select * from user where username = #{account} or email = #{account}")
    User selectByAccount(String account);

    /**
     * 根据邮箱查询用户
     */
    @Select("select * from user where email = #{email}")
    User selectByEmail(String email);

    /**
     * 新增用户
     */
    @Insert("insert into user(name,username, email, password, create_time, status) " +
            "values(#{name},#{username}, #{email}, #{password}, #{createTime}, #{status})")
    void insert(User user);

    /**
     * 查询用户名是否存在
     */
    @Select("select * from user where username = #{username}")
    User selectByUsername(String username);
}


