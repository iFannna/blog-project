package com.sau.utils.clients;

import com.aliyun.dm20151123.Client;
import com.aliyun.teaopenapi.models.Config;
import com.sau.utils.properties.AliyunEmailProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AliyunEmailClient {

    @Autowired
    private AliyunEmailProperties emailProperties;

    /**
     * 创建邮件客户端
     */
    public Client createClient() throws Exception {

        // 从环境变量中获取访问凭证
        com.aliyun.credentials.Client credential = new com.aliyun.credentials.Client();

        Config config = new Config()
                .setCredential(credential)
                .setEndpoint(emailProperties.getEndpoint());

        return new Client(config);
    }
}