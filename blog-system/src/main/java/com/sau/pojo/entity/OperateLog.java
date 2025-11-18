package com.sau.pojo.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志封装类
 */
@Data
public class OperateLog {
    /**
     * 操作日志ID
     */
    private Integer id;
    /**
     * 操作人ID
     */
    private Integer operateUserId;
    /**
     * 操作时间
     */
    private LocalDateTime operateTime;
    /**
     * 操作的类名
     */
    private String className;
    /**
     * 操作的方法名
     */
    private String methodName;
    /**
     * 操作的方法参数
     */
    private String methodParams;
    /**
     * 方法返回值
     */
    private String returnValue;
    /**
     * 操作耗时
     */
    private Long costTime;

}