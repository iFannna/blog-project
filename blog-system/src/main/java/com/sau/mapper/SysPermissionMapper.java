package com.sau.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 系统权限数据访问层
 */
@Mapper
public interface SysPermissionMapper {

    /**
     * 根据用户 ID 查询启用状态下的权限编码列表
     */
    @Select("""
            select distinct p.perm_code
            from sys_permission p
            inner join sys_role_permission rp on rp.perm_id = p.id
            inner join sys_user_role ur on ur.role_id = rp.role_id
            inner join sys_role r on r.id = ur.role_id
            where ur.user_id = #{userId}
              and p.status = 1
              and r.status = 1
            """)
    List<String> selectPermissionCodesByUserId(Integer userId);
}
