package com.sau.utils.clients;

import com.aliyun.captcha20230305.Client;
import com.aliyun.teaopenapi.models.Config;
import com.sau.utils.properties.AliyunCaptchaProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AliyunCaptchaClient {

    @Autowired
    private AliyunCaptchaProperties captchaProperties;

    /**
     * 创建阿里云验证码客户端
     */
    public Client createClient() throws Exception {

        // 从环境变量中获取访问凭证
        com.aliyun.credentials.Client credential = new com.aliyun.credentials.Client();

        Config config = new Config()
                .setCredential(credential)
                .setEndpoint(captchaProperties.getEndpoint());


        return new Client(config);
    }
}