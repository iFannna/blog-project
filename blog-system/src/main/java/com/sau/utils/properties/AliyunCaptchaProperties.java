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
@ConfigurationProperties(prefix = "aliyun.captcha")
public class AliyunCaptchaProperties {
    /**
     * 验证码服务节点
     */
    private String endpoint;
    /**
     * 场景ID
     */
    private String sceneId;
    /**
     * 地区
     */
    private String region;
}