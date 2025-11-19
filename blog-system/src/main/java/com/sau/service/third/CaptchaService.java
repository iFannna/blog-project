package com.sau.service.third;

import com.aliyun.captcha20230305.Client;
import com.aliyun.captcha20230305.models.VerifyIntelligentCaptchaRequest;
import com.aliyun.captcha20230305.models.VerifyIntelligentCaptchaResponse;
import com.aliyun.teautil.models.RuntimeOptions;
import com.sau.pojo.VO.CaptchaVerifyVO;
import com.sau.utils.RedisUtils;
import com.sau.utils.clients.AliyunCaptchaClient;
import com.sau.utils.properties.AliyunCaptchaProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CaptchaService {

    @Autowired
    private AliyunCaptchaClient aliyunCaptchaClient;

    @Autowired
    private AliyunCaptchaProperties aliyunCaptchaProperties;

    @Autowired
    private RedisUtils redisUtils;

    /**
     * 验证captchaVerifyParam
     */
    public CaptchaVerifyVO verifyCaptcha(String captchaVerifyParam) {
        // 判断是否已经完成安全校验
        if (redisUtils.exists(captchaVerifyParam)) {
            log.info("{}已完成安全验证", captchaVerifyParam);
            return new CaptchaVerifyVO(true, null, "已完成安全验证");
        }
        try {
            Client client = aliyunCaptchaClient.createClient();

            VerifyIntelligentCaptchaRequest request = new VerifyIntelligentCaptchaRequest()
                    .setCaptchaVerifyParam(captchaVerifyParam)
                    .setSceneId(aliyunCaptchaProperties.getSceneId());

            RuntimeOptions runtime = new RuntimeOptions();
            VerifyIntelligentCaptchaResponse response = client.verifyIntelligentCaptchaWithOptions(request, runtime);
            String verifyCode = response.getBody().getResult().getVerifyCode();
            if (Boolean.TRUE.equals(response.getBody().getResult().getVerifyResult())) {
                log.info("阿里云安全验证成功,VerifyCode: {}", verifyCode);
                // 验证成功，将captchaVerifyParam保存到Redis中
                redisUtils.set(captchaVerifyParam, "captchaVerifyParam");
                return new CaptchaVerifyVO(true, verifyCode, "安全验证成功");
            } else {
                log.warn("阿里云安全验证失败,VerifyCode: {}", verifyCode);
                return new CaptchaVerifyVO(false, verifyCode, "安全校验失败");
            }
        } catch (Exception e) {
            log.error("验证码验证服务异常", e);
            return new CaptchaVerifyVO(false, "SERVICE_ERROR", "验证服务异常: " + e.getMessage());
        }
    }
}
