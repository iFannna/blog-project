package com.sau.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 系统权限实体。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysPermission {
    /** 权限 ID */
    private Integer id;
    /** 权限名称 */
    private String permName;
    /** 权限编码 */
    private String permCode;
    /** 资源类型 */
    private String resourceType;
    /** 接口 URL */
    private String url;
    /** 请求方法 */
    private String method;
    /** 父权限 ID */
    private Integer parentId;
    /** 权限状态 */
    private Integer status;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 更新时间 */
    private LocalDateTime updateTime;
}