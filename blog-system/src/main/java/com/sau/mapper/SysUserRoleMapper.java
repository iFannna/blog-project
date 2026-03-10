package com.sau.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户角色关联数据访问层
 */
@Mapper
public interface SysUserRoleMapper {

    /**
     * 新增用户与角色的关联关系
     */
    @Insert("insert into sys_user_role(user_id, role_id) values (#{userId}, #{roleId})")
    void insert(@Param("userId") Integer userId, @Param("roleId") Integer roleId);
}
