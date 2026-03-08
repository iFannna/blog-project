package com.sau.service.third;

import com.aliyun.captcha20230305.Client;
import com.aliyun.captcha20230305.models.VerifyIntelligentCaptchaRequest;
import com.aliyun.captcha20230305.models.VerifyIntelligentCaptchaResponse;
import com.aliyun.teautil.models.RuntimeOptions;
import com.sau.constants.EmailConstants;
import com.sau.constants.RedisKeyConstants;
import com.sau.pojo.VO.CaptchaVerifyVO;
import com.sau.utils.RedisUtils;
import com.sau.utils.clients.AliyunCaptchaClient;
import com.sau.utils.properties.AliyunCaptchaProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 阿里云行为验证码服务。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CaptchaService {

    private final AliyunCaptchaClient aliyunCaptchaClient;
    private final AliyunCaptchaProperties aliyunCaptchaProperties;
    private final RedisUtils redisUtils;

    public CaptchaVerifyVO verifyCaptcha(String captchaParam) {
        String captchaKey = RedisKeyConstants.captchaKey(captchaParam);
        if (redisUtils.exists(captchaKey)) {
            return new CaptchaVerifyVO(true, null, "验证码已校验通过");
        }
        try {
            Client client = aliyunCaptchaClient.createClient();
            VerifyIntelligentCaptchaRequest request = new VerifyIntelligentCaptchaRequest()
                    .setCaptchaVerifyParam(captchaParam)
                    .setSceneId(aliyunCaptchaProperties.getSceneId());

            VerifyIntelligentCaptchaResponse response = client.verifyIntelligentCaptchaWithOptions(request, new RuntimeOptions());
            String verifyCode = response.getBody().getResult().getVerifyCode();
            if (Boolean.TRUE.equals(response.getBody().getResult().getVerifyResult())) {
                log.info("阿里云安全验证成功, verifyCode={}", verifyCode);
                redisUtils.set(captchaKey, captchaParam, EmailConstants.CODE_EXPIRE);
                return new CaptchaVerifyVO(true, verifyCode, "安全验证成功");
            }
            log.warn("阿里云安全验证失败, verifyCode={}", verifyCode);
            return new CaptchaVerifyVO(false, verifyCode, "安全校验失败");
        } catch (Exception e) {
            log.error("验证码验证服务异常", e);
            return new CaptchaVerifyVO(false, "SERVICE_ERROR", "验证码服务异常: " + e.getMessage());
        }
    }
}