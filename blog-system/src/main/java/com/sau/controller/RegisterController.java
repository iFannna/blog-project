package com.sau.controller;

import com.sau.anno.Log;
import com.sau.pojo.DTO.RegisterDTO;
import com.sau.pojo.VO.CaptchaVerifyVO;
import com.sau.pojo.entity.Result;
import com.sau.service.UserService;
import com.sau.service.third.CaptchaService;
import com.sau.service.third.EmailService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


/**
 * 注册相关接口
 */
@Slf4j
@RestController
public class RegisterController {

    @Autowired
    private UserService userService;

    // 阿里云邮件推送服务
    @Autowired
    private EmailService emailService;

    // 阿里云验证码安全校验服务
    @Autowired
    private CaptchaService captchaService;

    /**
     * 发送邮件验证码
     */
    @Log
    @PostMapping("/send-register-code")
    public Result sendRegisterCode(@RequestBody RegisterDTO request) {

        // 1. 安全校验
        CaptchaVerifyVO captchaResult = captchaService.verifyCaptcha(request.getCaptchaParams());
        if (!captchaResult.isSuccess()) {
            return Result.error(captchaResult.getMessage());
        }

        boolean result = emailService.sendRegisterCode(request);
        if (!result){
            return Result.error("验证码发送失败，请稍后重试");
        }
        return Result.success();

    }

    /**
     * 用户注册
     */
    @Log
    @PostMapping("/register")
    public Result register(@Valid @RequestBody RegisterDTO request) {
        log.info("用户注册请求：{}", request);
        // 判断邮箱验证码是否正确
        if (!emailService.verifyCode(request.getEmail(), request.getEmailVerificationCode())){
            return Result.error("验证码错误");
        }
        userService.register(request);
        return Result.success();
    }
}