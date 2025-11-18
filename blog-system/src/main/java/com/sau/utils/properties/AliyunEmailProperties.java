package com.sau.utils.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "aliyun.email")
public class AliyunEmailProperties {
    /**
     * 邮件推送服务节点
     */
    private String endpoint;
    /**
     * 发件人账号
     */
    private String accountName;
    /**
     * 发件人别名
     */
    private String fromAlias;
}