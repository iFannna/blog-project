package com.sau.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 系统角色实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysRole {
    /** 角色 ID */
    private Integer id;
    /** 角色名称 */
    private String roleName;
    /** 角色编码 */
    private String roleCode;
    /** 角色状态 */
    private Integer status;
    /** 角色描述 */
    private String description;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 更新时间 */
    private LocalDateTime updateTime;
}
