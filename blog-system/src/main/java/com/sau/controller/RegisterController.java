package com.sau.controller;

import com.sau.annotation.Log;
import com.sau.pojo.VO.CaptchaVerifyVO;
import com.sau.pojo.DTO.RegisterDTO;
import com.sau.pojo.entity.Result;
import com.sau.service.third.CaptchaService;
import com.sau.service.third.EmailService;
import com.sau.service.SysUserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 注册相关接口
 */
@Slf4j
@RestController
public class RegisterController {

    @Autowired
    private SysUserService sysUserService;

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
    @GetMapping("/send-register-code")
    public Result sendRegisterCode(String email, String captchaParam ) {

        // 1. 安全校验
        CaptchaVerifyVO captchaResult = captchaService.verifyCaptcha(captchaParam);
        if (!captchaResult.isSuccess()) {
            return Result.error("安全验证失败，请重试");
        }

        // 2. 发送邮件验证码
        boolean success = emailService.sendEmailCode(email);
        return success ?
                Result.success() :
                Result.error("验证码发送失败，请稍后重试");
    }

    /**
     * 用户注册
     */
    @Log
    @PostMapping("/register")
    public Result register(@Valid @RequestBody RegisterDTO registerDTO) {
        log.info("用户注册请求：{}", registerDTO);
        // 判断邮箱验证码是否正确
        if (!emailService.verifyCode(registerDTO.getEmail(), registerDTO.getEmailVerificationCode())){
            return Result.error("验证码错误或已失效");
        }
        sysUserService.register(registerDTO);
        return Result.success();
    }
}