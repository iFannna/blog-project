package com.sau.aop;

import com.sau.mapper.OperateLogMapper;
import com.sau.pojo.entity.OperateLog;
import com.sau.utils.CurrentHolderUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * 操作日志AOP
 */
@Slf4j
@Aspect
@Component
public class OperationLogAspect {

    @Autowired
    private OperateLogMapper operateLogMapper;

    @Around("@annotation(com.sau.anno.Log)")
    public Object logOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        // 执行目标方法
        Object result = joinPoint.proceed();

        // 计算耗时
        long endTime = System.currentTimeMillis();
        long costTime = endTime - startTime;

        // 构建日志实体
        OperateLog olog = new OperateLog();
        olog.setOperateUserId(getCurrentUserId());
        olog.setOperateTime(LocalDateTime.now());
        olog.setClassName(joinPoint.getTarget().getClass().getName());
        olog.setMethodName(joinPoint.getSignature().getName());
        olog.setMethodParams(Arrays.toString(joinPoint.getArgs()));
        olog.setReturnValue(result != null? result.toString() : "void");
        olog.setCostTime(costTime);

        // 保存日志
        log.info("记录操作日志: {}", olog);
        operateLogMapper.insert(olog);

        return result;
    }

    private Integer getCurrentUserId() {
        // 实现获取当前登录用户的逻辑
        return CurrentHolderUtils.getCurrentId();
    }
}