// com/sau/service/EmailService.java
package com.sau.service.third;

import com.aliyun.dm20151123.Client;
import com.aliyun.dm20151123.models.SingleSendMailRequest;
import com.aliyun.teautil.models.RuntimeOptions;
import com.sau.utils.clients.AliyunEmailClient;
import com.sau.utils.properties.AliyunEmailProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class EmailService {

    @Autowired
    private AliyunEmailClient emailClient;

    @Autowired
    private AliyunEmailProperties emailProperties;

    @Autowired
    private StringRedisTemplate redisTemplate;

    // 注入资源加载器，用于读取HTML模板文件
    @Autowired
    private ResourceLoader resourceLoader;

    // 验证码有效期：5分钟
    private static final long CODE_EXPIRE = 5;

    // HTML模板路径
    private static final String REGISTER_CODE_TEMPLATE_PATH = "classpath:templates/register-code-template.html";

    /**
     * 生成6位数字验证码
     */
    private String generateCode() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    /**
     * 发送注册验证码邮件
     */
    public boolean sendRegisterCode(String email) {
        try {
            // 生成验证码并存储到Redis
            String code = generateCode();
            String redisKey = "register:code:" + email;

            redisTemplate.opsForValue().set(redisKey, code, CODE_EXPIRE, TimeUnit.MINUTES);

            // 构建邮件请求
            Client client = emailClient.createClient();
            SingleSendMailRequest request = new SingleSendMailRequest()
                    .setAccountName(emailProperties.getAccountName())
                    .setAddressType(1)  // 1:随机账号类型
                    .setToAddress(email)
                    .setSubject("【创图AI】 注册验证码（5分钟内有效）")
                    .setFromAlias(emailProperties.getFromAlias())
                    // 邮件内容
                    .setHtmlBody(buildEmailContent(code))
                    .setReplyToAddress(false);

            // 发送邮件
            client.singleSendMailWithOptions(request, new RuntimeOptions());

            log.info("邮件验证码发送成功，邮箱：{}", email);
            return true;

        } catch (Exception e) {

            log.error("邮件发送失败，邮箱：{}", email, e);
            return false;
        }
    }

    /**
     * 验证邮件验证码
     */
    public boolean verifyCode(String email, String code) {
        if (code == null || email == null) {
            return false;
        }

        String redisKey = "register:code:" + email;
        String storedCode = redisTemplate.opsForValue().get(redisKey);
        
        // 验证通过后删除验证码
        if (code.equals(storedCode)) {
            redisTemplate.delete(redisKey);
            return true;
        }
        return false;
    }


    /**
     * 从模板加载并构建邮件HTML内容
     */
    private String buildEmailContent(String code) throws IOException {
        // 读取HTML模板文件
        String templateContent = loadHtmlTemplate(REGISTER_CODE_TEMPLATE_PATH);
        // 替换模板中的验证码占位符
        return templateContent.replace("${code}", code);
    }

    /**
     * 加载HTML模板文件内容
     */
    private String loadHtmlTemplate(String templatePath) throws IOException {
        // 加载资源文件
        Resource resource = resourceLoader.getResource(templatePath);
        // 读取文件内容
        byte[] contentBytes = FileCopyUtils.copyToByteArray(resource.getInputStream());
        return new String(contentBytes, StandardCharsets.UTF_8);
    }
}
