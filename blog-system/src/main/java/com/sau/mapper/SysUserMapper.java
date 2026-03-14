package com.sau.mapper;

import com.sau.pojo.entity.SysUser;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SysUserMapper {

    @Select("select * from sys_user where username = #{account} or email = #{account}")
    SysUser selectByAccount(String account);

    @Select("select * from sys_user where id = #{id}")
    SysUser selectById(Integer id);

    @Select("select * from sys_user where email = #{email}")
    SysUser selectByEmail(String email);

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into sys_user(nickname, username, email, password, status) values(#{nickname}, #{username}, #{email}, #{password}, #{status})")
    void insert(SysUser sysUser);

    @Select("select * from sys_user where username = #{username}")
    SysUser selectByUsername(String username);

    @Update("update sys_user set nickname = #{nickname}, introduction = #{introduction}, phone = #{phone}, avatar = #{avatar} where id = #{id}")
    void updateProfileById(SysUser sysUser);

    @Update("update sys_user set password = #{password} where id = #{id}")
    void updatePasswordById(SysUser sysUser);

    @Select("select email from sys_user where username = #{username}")
    String getEmailByUsername(String username);

    @Select("select email from sys_user where id = #{userId}")
    String getEmailByUserId(Integer userId);

    @Update("update sys_user set email = #{email} where id = #{id}")
    void updateEmailById(SysUser sysUser);
}
