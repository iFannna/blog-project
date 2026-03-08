package com.sau.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户角色关联实体。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysUserRole {
    /** 用户 ID */
    private Integer userId;
    /** 角色 ID */
    private Integer roleId;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 更新时间 */
    private LocalDateTime updateTime;
}