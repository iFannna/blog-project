package com.sau.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 系统角色数据访问层
 */
@Mapper
public interface SysRoleMapper {

    /**
     * 根据用户 ID 查询启用状态下的角色编码列表
     */
    @Select("""
            select r.role_code
            from sys_role r
            inner join sys_user_role ur on ur.role_id = r.id
            where ur.user_id = #{userId} and r.status = 1
            """)
    List<String> selectRoleCodesByUserId(Integer userId);

    /**
     * 根据角色编码查询角色 ID
     */
    @Select("select id from sys_role where role_code = #{roleCode} and status = 1 limit 1")
    Integer selectIdByRoleCode(@Param("roleCode") String roleCode);
}
