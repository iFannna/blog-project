package com.sau.mapper;

import com.sau.pojo.entity.SysUser;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SysUserMapper {

    /**
     * 查询用户
     */
    @Select("select * from user where username = #{account} or email = #{account}")
    SysUser selectByAccount(String account);

    /**
     * 根据邮箱查询用户
     */
    @Select("select * from user where email = #{email}")
    SysUser selectByEmail(String email);

    /**
     * 新增用户
     */
    @Insert("insert into user(nickname,username, email, password, create_time, status) " +
            "values(#{nickname},#{username}, #{email}, #{password}, #{createTime}, #{status})")
    void insert(SysUser sysUser);

    /**
     * 查询用户名是否存在
     */
    @Select("select * from user where username = #{username}")
    SysUser selectByUsername(String username);

    /**
     * 修改用户信息
     */
    @Update("update user set nickname = #{nickname}, introduction = #{introduction}, avatar = #{avatar} where id = #{id}")
    void updateProfiles(SysUser sysUser);

    /**
     * 修改用户密码
     */
    @Update("update user set password = #{password} where username = #{username}")
    void updatePassword(SysUser sysUser);

    /**
     * 根据用户名查询用户邮箱
     */
    @Select("select email from user where username = #{username}")
    String getEmailByUsername(String username);

    /**
     * 修改用户邮箱
     */
    @Update("update user set email = #{email} where username = #{username}")
    void updateEmail(SysUser sysUser);
}


