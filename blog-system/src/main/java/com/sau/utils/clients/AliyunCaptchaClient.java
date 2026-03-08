package com.sau.utils.clients;

import com.aliyun.captcha20230305.Client;
import com.aliyun.teaopenapi.models.Config;
import com.sau.utils.properties.AliyunCaptchaProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 阿里云验证码客户端工厂。
 */
@Component
@RequiredArgsConstructor
public class AliyunCaptchaClient {

    private final AliyunCaptchaProperties captchaProperties;

    public Client createClient() throws Exception {
        com.aliyun.credentials.Client credential = new com.aliyun.credentials.Client();
        Config config = new Config()
                .setCredential(credential)
                .setEndpoint(captchaProperties.getEndpoint());
        return new Client(config);
    }
}