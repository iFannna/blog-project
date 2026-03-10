package com.sau.controller;

import lombok.RequiredArgsConstructor;

import com.sau.annotation.Log;
import com.sau.pojo.DTO.RegisterDTO;
import com.sau.pojo.VO.CaptchaVerifyVO;
import com.sau.pojo.entity.Result;
import com.sau.service.SysUserService;
import com.sau.service.third.CaptchaService;
import com.sau.service.third.EmailService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 注册相关接口
 */
@Slf4j
@RequiredArgsConstructor
@RestController
public class RegisterController {
    private final SysUserService sysUserService;
    private final EmailService emailService;
    private final CaptchaService captchaService;

    /**
     * 发送注册验证码
     */
    @Log
    @GetMapping("/send-register-code")
    public Result sendRegisterCode(String email, String captchaParam) {
        CaptchaVerifyVO captchaResult = captchaService.verifyCaptcha(captchaParam);
        if (!captchaResult.isSuccess()) {
            return Result.error("安全验证失败，请重试");
        }

        boolean success = emailService.sendEmailCode(email);
        return success ? Result.success() : Result.error("验证码发送失败，请稍后重试");
    }

    /**
     * 用户注册
     */
    @Log
    @PostMapping("/register")
    public Result register(@Valid @RequestBody RegisterDTO registerDTO) {
        log.info("用户注册请求: {}", registerDTO);
        if (!emailService.verifyCode(registerDTO.getEmail(), registerDTO.getEmailVerificationCode())) {
            return Result.error("验证码错误或已失效");
        }
        sysUserService.register(registerDTO);
        return Result.success();
    }
}
