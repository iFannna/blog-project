package com.sau.utils.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
@ConfigurationProperties(prefix = "aliyun.oss")
public class AliyunOSSProperties {
    /**
     * OSS对象存储服务节点
     */
    private String endpoint;
    /**
     * 存储空间名称
     */
    private String bucketName;
    /**
     * 地区
     */
    private String region;
}