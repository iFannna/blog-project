package com.sau.service.third;

import com.aliyun.dm20151123.Client;
import com.aliyun.dm20151123.models.SingleSendMailRequest;
import com.aliyun.teautil.models.RuntimeOptions;
import com.sau.constants.EmailConstants;
import com.sau.utils.RedisUtils;
import com.sau.utils.clients.AliyunEmailClient;
import com.sau.utils.properties.AliyunEmailProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Random;


@Slf4j
@Service
public class EmailService {

    @Autowired
    private AliyunEmailClient emailClient;

    @Autowired
    private AliyunEmailProperties emailProperties;

    @Autowired
    private RedisUtils redisUtils;

    // 注入资源加载器，用于读取HTML模板文件
    @Autowired
    private ResourceLoader resourceLoader;


    /**
     * 生成6位数字验证码
     */
    private String generateCode() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    /**
     * 发送验证码邮件
     */
    public boolean sendEmailCode(String email) {
        // 判断redis中是否存在该邮箱验证码冷却
        if (redisUtils.exists("email:code:" + email + ":cooling")) {
            log.warn("邮箱验证码冷却中，请稍后再试");
            return false;
        }
        try {
            // 生成验证码并存储到Redis
            String code = generateCode();
            String redisKey = "email:code:" + email;
            redisUtils.set(redisKey, code, EmailConstants.CODE_EXPIRE);
            // 设置该邮件验证码的冷却时间
            redisUtils.set(redisKey + ":cooling", email, EmailConstants.SEND_COOLDOWN);

            // 构建邮件请求
            Client client = emailClient.createClient();
            SingleSendMailRequest request = new SingleSendMailRequest()
                    .setAccountName(emailProperties.getAccountName())
                    .setAddressType(1)  // 1:随机账号类型
                    .setToAddress(email)
                    .setSubject("验证码（5分钟内有效）")
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

        String redisKey = "email:code:" + email;
        String storedCode = (String) redisUtils.get(redisKey);

        // 验证通过后删除验证码
        if (code.equals(storedCode)) {
            redisUtils.delete(redisKey);
            return true;
        }
        return false;
    }


    /**
     * 从模板加载并构建邮件HTML内容
     */
    private String buildEmailContent(String code) throws IOException {
        // 1. 加载HTML模板文件
        Resource resource = resourceLoader.getResource(EmailConstants.EMAIL_CODE_TEMPLATE_PATH);
        byte[] contentBytes = FileCopyUtils.copyToByteArray(resource.getInputStream());
        String templateContent = new String(contentBytes, StandardCharsets.UTF_8);

        // 2. 替换模板中的验证码占位符
        return templateContent.replace("${code}", code);
    }
}
