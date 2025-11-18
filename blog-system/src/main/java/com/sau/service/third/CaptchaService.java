package com.sau.service.third;

import com.aliyun.captcha20230305.Client;
import com.aliyun.captcha20230305.models.VerifyIntelligentCaptchaRequest;
import com.aliyun.captcha20230305.models.VerifyIntelligentCaptchaResponse;
import com.aliyun.teautil.models.RuntimeOptions;
import com.sau.pojo.VO.CaptchaVerifyVO;
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

    /**
     * 验证智能验证码
     * @param captchaVerifyParam 前端传回的验证参数
     * @return 验证结果
     */
    public boolean verifyCaptcha(String captchaVerifyParam) {
        try {
            Client client = aliyunCaptchaClient.createClient();

            VerifyIntelligentCaptchaRequest request = new VerifyIntelligentCaptchaRequest()
                    .setCaptchaVerifyParam(captchaVerifyParam)
                    .setSceneId(aliyunCaptchaProperties.getSceneId());

            RuntimeOptions runtime = new RuntimeOptions();
            VerifyIntelligentCaptchaResponse response = client.verifyIntelligentCaptchaWithOptions(request, runtime);

            // 返回结果中Result字段的VerifyResult为true表示验证成功
            if (response.getBody() != null &&
                    response.getBody().getResult() != null &&
                    Boolean.TRUE.equals(response.getBody().getResult().getVerifyResult())) {
                log.info("验证码验证成功，VerifyCode: {}", response.getBody().getResult().getVerifyCode());
                return true;
            } else {
                log.warn("验证码验证失败，VerifyCode: {}",
                        response.getBody() != null && response.getBody().getResult() != null ?
                                response.getBody().getResult().getVerifyCode() : "未知错误");
                return false;
            }
        } catch (Exception e) {
            log.error("验证码验证服务异常", e);
            return false;
        }
    }

    /**
     * 验证智能验证码（带详细错误信息）
     * @param captchaVerifyParam 前端传回的验证参数
     * @return 验证结果对象
     */
    public CaptchaVerifyVO verifyCaptchaWithDetail(String captchaVerifyParam) {
        try {
            Client client = aliyunCaptchaClient.createClient();

            VerifyIntelligentCaptchaRequest request = new VerifyIntelligentCaptchaRequest()
                    .setCaptchaVerifyParam(captchaVerifyParam)
                    .setSceneId(aliyunCaptchaProperties.getSceneId());

            RuntimeOptions runtime = new RuntimeOptions();
            VerifyIntelligentCaptchaResponse response = client.verifyIntelligentCaptchaWithOptions(request, runtime);

            if (response.getBody() != null && response.getBody().getResult() != null) {
                boolean success = Boolean.TRUE.equals(response.getBody().getResult().getVerifyResult());
                String verifyCode = response.getBody().getResult().getVerifyCode();

                log.info("验证码验证结果: {}, VerifyCode: {}", success ? "成功" : "失败", verifyCode);

                return new CaptchaVerifyVO(success, verifyCode, "验证完成");
            } else {
                log.warn("验证码验证返回结果为空");
                return new CaptchaVerifyVO(false, "EMPTY_RESPONSE", "验证服务返回结果为空");
            }
        } catch (Exception e) {
            log.error("验证码验证服务异常", e);
            return new CaptchaVerifyVO(false, "SERVICE_ERROR", "验证服务异常: " + e.getMessage());
        }
    }
}
