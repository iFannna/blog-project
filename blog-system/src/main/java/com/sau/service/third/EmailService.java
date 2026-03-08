package com.sau.service.third;

import com.aliyun.dm20151123.Client;
import com.aliyun.dm20151123.models.SingleSendMailRequest;
import com.aliyun.teautil.models.RuntimeOptions;
import com.sau.constants.EmailConstants;
import com.sau.constants.RedisKeyConstants;
import com.sau.utils.RedisUtils;
import com.sau.utils.clients.AliyunEmailClient;
import com.sau.utils.properties.AliyunEmailProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 邮件验证码服务。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final AliyunEmailClient emailClient;
    private final AliyunEmailProperties emailProperties;
    private final RedisUtils redisUtils;
    private final ResourceLoader resourceLoader;

    private String generateCode() {
        return String.format("%06d", ThreadLocalRandom.current().nextInt(1_000_000));
    }

    public boolean sendEmailCode(String email) {
        String codeKey = RedisKeyConstants.emailCodeKey(email);
        String cooldownKey = RedisKeyConstants.emailCodeCooldownKey(email);
        if (redisUtils.exists(cooldownKey)) {
            log.warn("邮箱验证码冷却中，请稍后再试");
            return false;
        }
        try {
            String code = generateCode();
            redisUtils.set(codeKey, code, EmailConstants.CODE_EXPIRE);
            redisUtils.set(cooldownKey, email, EmailConstants.SEND_COOLDOWN);

            Client client = emailClient.createClient();
            SingleSendMailRequest request = new SingleSendMailRequest()
                    .setAccountName(emailProperties.getAccountName())
                    .setAddressType(1)
                    .setToAddress(email)
                    .setSubject("验证码（5分钟内有效）")
                    .setFromAlias(emailProperties.getFromAlias())
                    .setHtmlBody(buildEmailContent(code))
                    .setReplyToAddress(false);

            client.singleSendMailWithOptions(request, new RuntimeOptions());
            log.info("邮件验证码发送成功，邮箱:{}", email);
            return true;
        } catch (Exception e) {
            log.error("邮件发送失败，邮箱:{}", email, e);
            return false;
        }
    }

    public boolean verifyCode(String email, String code) {
        if (email == null || code == null) {
            return false;
        }

        String codeKey = RedisKeyConstants.emailCodeKey(email);
        Object storedCode = redisUtils.get(codeKey);
        if (code.equals(storedCode)) {
            redisUtils.delete(codeKey);
            return true;
        }
        return false;
    }

    private String buildEmailContent(String code) throws IOException {
        Resource resource = resourceLoader.getResource(EmailConstants.EMAIL_CODE_TEMPLATE_PATH);
        byte[] contentBytes = FileCopyUtils.copyToByteArray(resource.getInputStream());
        String templateContent = new String(contentBytes, StandardCharsets.UTF_8);
        return templateContent.replace("${code}", code);
    }
}