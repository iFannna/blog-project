package com.sau.service.third;

import com.aliyun.dm20151123.Client;
import com.aliyun.dm20151123.models.SingleSendMailRequest;
import com.aliyun.teautil.models.RuntimeOptions;
import com.sau.constants.EmailConstants;
import com.sau.pojo.DTO.RegisterDTO;
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
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class EmailService {

    @Autowired
    private AliyunEmailClient emailClient;

    @Autowired
    private AliyunEmailProperties emailProperties;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private ResourceLoader resourceLoader;


    /**
     * 生成6位数字验证码
     */
    private String generateCode() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    /**
     * 发送注册验证码邮件（增加防刷机制）
     */
    public boolean sendRegisterCode(RegisterDTO registerDTO) {
        String email = registerDTO.getEmail();
        // 定义Redis Key：验证码Key + 发送冷却Key
        String codeKey = "register:code:" + email;
        String cooldownKey = "register:cooldown:" + email;

        try {
            // 1. 防刷校验：检查是否在60秒冷却期内
            if (redisUtils.exists(cooldownKey)) {
                // 冷却期内发起二次请求，直接失效旧验证码
                redisUtils.delete(codeKey);
                log.warn("邮箱{}处于60秒发送冷却期，已失效旧验证码，请勿重复发送", email);
                return false;
            }

            // 2. 生成新验证码（覆盖旧验证码，自然使其失效）
            String code = generateCode();
            redisUtils.set(codeKey, code, EmailConstants.CODE_EXPIRE);
            log.info("邮箱{}的验证码已更新为：{}，有效期5分钟", email, code);

            // 3. 设置发送冷却Key（60秒过期，核心防刷）
            redisUtils.set(cooldownKey, "lock", EmailConstants.SEND_COOLDOWN);

            // 4. 构建邮件请求并发送
            Client client = emailClient.createClient();
            SingleSendMailRequest request = new SingleSendMailRequest()
                    .setAccountName(emailProperties.getAccountName())
                    .setAddressType(1)  // 1:随机账号类型
                    .setToAddress(email)
                    .setSubject("注册验证码（5分钟内有效）")
                    .setFromAlias(emailProperties.getFromAlias())
                    .setHtmlBody(buildEmailContent(code))
                    .setReplyToAddress(false);

            // 发送邮件
            client.singleSendMailWithOptions(request, new RuntimeOptions());

            log.info("邮箱{}的注册验证码邮件发送成功", email);
            return true;
        } catch (Exception e) {
            log.error("邮箱{}的验证码邮件发送失败", email, e);
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

        String codeKey = "register:code:" + email;
        String storedCode = (String) redisUtils.get(codeKey);

        // 验证通过后删除验证码（防止重复使用）
        if (code.equals(storedCode)) {
            redisUtils.delete(codeKey);
            log.info("邮箱{}验证成功，已删除验证码缓存", email);
            return true;
        }
        log.warn("邮箱{}验证码错误", email);
        return false;
    }

    /**
     * 从模板加载并构建邮件HTML内容
     */
    private String buildEmailContent(String code) throws IOException {
        String templateContent = loadHtmlTemplate(EmailConstants.REGISTER_CODE_TEMPLATE_PATH);
        return templateContent.replace("${code}", code);
    }

    /**
     * 加载HTML模板文件内容
     */
    private String loadHtmlTemplate(String templatePath) throws IOException {
        Resource resource = resourceLoader.getResource(templatePath);
        byte[] contentBytes = FileCopyUtils.copyToByteArray(resource.getInputStream());
        return new String(contentBytes, StandardCharsets.UTF_8);
    }
}