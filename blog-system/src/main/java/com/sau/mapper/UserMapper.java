package com.sau.mapper;

import com.sau.pojo.DTO.UserPasswordUpdateDTO;
import com.sau.pojo.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

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
    @Insert("insert into user(nickname,username, email, password, create_time, status) " +
            "values(#{nickname},#{username}, #{email}, #{password}, #{createTime}, #{status})")
    void insert(User user);

    /**
     * 查询用户名是否存在
     */
    @Select("select * from user where username = #{username}")
    User selectByUsername(String username);

    /**
     * 修改用户信息
     */
    @Update("update user set nickname = #{nickname}, introduction = #{introduction}, avatar = #{avatar} where id = #{id}")
    void updateProfiles(User user);

    /**
     * 修改用户密码
     */
    @Update("update user set password = #{password} where username = #{username}")
    void updatePassword(User user);

    /**
     * 根据用户名查询用户邮箱
     */
    @Select("select email from user where username = #{username}")
    String getEmailByUsername(String username);

    /**
     * 修改用户邮箱
     */
    @Update("update user set email = #{email} where username = #{username}")
    void updateEmail(User user);
}


