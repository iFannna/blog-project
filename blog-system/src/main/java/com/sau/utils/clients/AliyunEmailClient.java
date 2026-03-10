package com.sau.utils.clients;

import com.aliyun.dm20151123.Client;
import com.aliyun.teaopenapi.models.Config;
import com.sau.utils.properties.AliyunEmailProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 阿里云邮件客户端工厂
 */
@Component
@RequiredArgsConstructor
public class AliyunEmailClient {

    private final AliyunEmailProperties emailProperties;

    public Client createClient() throws Exception {
        com.aliyun.credentials.Client credential = new com.aliyun.credentials.Client();
        Config config = new Config()
                .setCredential(credential)
                .setEndpoint(emailProperties.getEndpoint());
        return new Client(config);
    }
}
