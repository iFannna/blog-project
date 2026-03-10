package com.sau.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 角色权限关联实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysRolePermission {
    /** 角色 ID */
    private Integer roleId;
    /** 权限 ID */
    private Integer permId;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 更新时间 */
    private LocalDateTime updateTime;
}
