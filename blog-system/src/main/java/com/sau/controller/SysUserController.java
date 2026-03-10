package com.sau.controller;

import lombok.RequiredArgsConstructor;

import com.sau.pojo.DTO.SendNewEmailCodeDTO;
import com.sau.pojo.DTO.SendOldEmailCodeDTO;
import com.sau.pojo.DTO.UserEmailUpdateDTO;
import com.sau.pojo.DTO.UserPasswordUpdateDTO;
import com.sau.pojo.DTO.UserProfilesUpdateDTO;
import com.sau.pojo.DTO.VerifyOldEmailCodeDTO;
import com.sau.pojo.VO.CaptchaVerifyVO;
import com.sau.pojo.entity.Result;
import com.sau.service.SysUserService;
import com.sau.service.third.CaptchaService;
import com.sau.service.third.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户相关接口
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class SysUserController {
    private final SysUserService sysUserService;
    private final EmailService emailService;
    private final CaptchaService captchaService;

    /**
     * 修改个人资料
     */
    @PreAuthorize("hasAuthority('user:profile:update')")
    @PutMapping(value = "/profiles", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result updateProfile(@ModelAttribute UserProfilesUpdateDTO userProfilesUpdateDTO) {
        log.info("修改个人资料");
        return sysUserService.updateProfile(userProfilesUpdateDTO);
    }

    /**
     * 修改密码
     */
    @PreAuthorize("hasAuthority('user:password:update')")
    @PutMapping("/password")
    public Result updatePassword(@RequestBody UserPasswordUpdateDTO userPasswordUpdateDTO) {
        return sysUserService.updatePassword(userPasswordUpdateDTO);
    }

    /**
     * 向旧邮箱发送验证码
     */
    @PreAuthorize("hasAuthority('user:email:update')")
    @PostMapping("/email/send-old-code")
    public Result sendOldEmailCode(@RequestBody SendOldEmailCodeDTO sendOldEmailCodeDTO) {
        CaptchaVerifyVO captchaResult = captchaService.verifyCaptcha(sendOldEmailCodeDTO.getCaptchaParam());
        if (!captchaResult.isSuccess()) {
            return Result.error("安全验证失败，请重试");
        }
        String dbEmail = sysUserService.getCurrentUserEmail();
        if (!dbEmail.equals(sendOldEmailCodeDTO.getOldEmail())) {
            return Result.error("邮箱错误");
        }
        boolean success = emailService.sendEmailCode(sendOldEmailCodeDTO.getOldEmail());
        return success ? Result.success() : Result.error("验证码发送失败，请稍后重试");
    }

    /**
     * 校验旧邮箱验证码
     */
    @PreAuthorize("hasAuthority('user:email:update')")
    @PostMapping("/email/verify-old-code")
    public Result verifyOldEmailCode(@RequestBody VerifyOldEmailCodeDTO verifyOldEmailCodeDTO) {
        if (!emailService.verifyCode(verifyOldEmailCodeDTO.getOldEmail(), verifyOldEmailCodeDTO.getOldEmailCode())) {
            return Result.error("验证码错误或已失效");
        }
        String token = sysUserService.generateTempEmailChangeToken(verifyOldEmailCodeDTO);
        return Result.success(token);
    }

    /**
     * 向新邮箱发送验证码
     */
    @PreAuthorize("hasAuthority('user:email:update')")
    @PostMapping("/email/send-new-code")
    public Result sendNewEmailCode(@RequestBody SendNewEmailCodeDTO sendNewEmailCodeDTO) {
        if (sysUserService.hasEmail(sendNewEmailCodeDTO.getNewEmail())) {
            return Result.error("该邮箱已被使用");
        }
        if (!sysUserService.verifyTempEmailChangeToken(sendNewEmailCodeDTO.getTempToken())) {
            return Result.error("令牌已失效");
        }
        boolean success = emailService.sendEmailCode(sendNewEmailCodeDTO.getNewEmail());
        return success ? Result.success() : Result.error("验证码发送失败，请稍后重试");
    }

    /**
     * 更新邮箱
     */
    @PreAuthorize("hasAuthority('user:email:update')")
    @PutMapping("/email/update")
    public Result updateEmail(@RequestBody UserEmailUpdateDTO userEmailUpdateDTO) {
        if (!sysUserService.verifyTempEmailChangeToken(userEmailUpdateDTO.getTempToken())) {
            return Result.error("令牌已失效");
        }
        if (!emailService.verifyCode(userEmailUpdateDTO.getNewEmail(), userEmailUpdateDTO.getNewEmailCode())) {
            return Result.error("验证码错误或已失效");
        }

        sysUserService.updateEmail(userEmailUpdateDTO);
        return Result.success();
    }
}
