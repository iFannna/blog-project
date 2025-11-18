package com.sau.mapper;

import com.sau.pojo.entity.OperateLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OperateLogMapper {

    /**
     * 插入日志数据
     */
    @Insert("insert into operate_log(operate_user_id, operate_time, class_name, method_name, method_params, return_value, cost_time) " +
            "value (#{operateUserId}, #{operateTime}, #{className}, #{methodName}, #{methodParams}, #{returnValue}, #{costTime})")
    void insert(OperateLog log);
}
