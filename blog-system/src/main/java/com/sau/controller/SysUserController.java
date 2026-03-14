package com.sau.controller;

import com.sau.pojo.DTO.SendNewEmailCodeDTO;
import com.sau.pojo.DTO.SendOldEmailCodeDTO;
import com.sau.pojo.DTO.UserEmailUpdateDTO;
import com.sau.pojo.DTO.UserPasswordUpdateDTO;
import com.sau.pojo.DTO.UserProfilesUpdateDTO;
import com.sau.pojo.DTO.VerifyOldEmailCodeDTO;
import com.sau.pojo.VO.CaptchaVerifyVO;
import com.sau.pojo.VO.UserProfileVO;
import com.sau.pojo.entity.Result;
import com.sau.service.SysUserService;
import com.sau.service.third.CaptchaService;
import com.sau.service.third.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class SysUserController {
    private final SysUserService sysUserService;
    private final EmailService emailService;
    private final CaptchaService captchaService;

    @GetMapping("/profile")
    public Result<UserProfileVO> getCurrentProfile() {
        UserProfileVO userProfileVO = sysUserService.getCurrentProfile();
        if (userProfileVO == null) {
            return Result.error("用户不存在");
        }
        return Result.success(userProfileVO);
    }

    @PreAuthorize("hasAuthority('user:profile:update')")
    @PutMapping(value = "/profiles", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result updateProfile(@ModelAttribute UserProfilesUpdateDTO userProfilesUpdateDTO) {
        log.info("Update user profile");
        return sysUserService.updateProfile(userProfilesUpdateDTO);
    }

    @PreAuthorize("hasAuthority('user:password:update')")
    @PutMapping("/password")
    public Result updatePassword(@Valid @RequestBody UserPasswordUpdateDTO userPasswordUpdateDTO) {
        return sysUserService.updatePassword(userPasswordUpdateDTO);
    }

    @PreAuthorize("hasAuthority('user:email:update')")
    @PostMapping("/email/send-old-code")
    public Result sendOldEmailCode(@Valid @RequestBody SendOldEmailCodeDTO sendOldEmailCodeDTO) {
        CaptchaVerifyVO captchaResult = captchaService.verifyCaptcha(sendOldEmailCodeDTO.getCaptchaParam());
        if (!captchaResult.isSuccess()) {
            return Result.error("安全验证失败，请重试");
        }

        String dbEmail = sysUserService.getCurrentUserEmail();
        if (!dbEmail.equals(sendOldEmailCodeDTO.getOldEmail())) {
            return Result.error("旧邮箱与当前账号不匹配");
        }

        boolean success = emailService.sendEmailCode(sendOldEmailCodeDTO.getOldEmail());
        return success ? Result.success() : Result.error("验证码发送失败");
    }

    @PreAuthorize("hasAuthority('user:email:update')")
    @PostMapping("/email/verify-old-code")
    public Result verifyOldEmailCode(@Valid @RequestBody VerifyOldEmailCodeDTO verifyOldEmailCodeDTO) {
        if (!emailService.verifyCode(verifyOldEmailCodeDTO.getOldEmail(), verifyOldEmailCodeDTO.getOldEmailCode())) {
            return Result.error("验证码无效或已过期");
        }

        String token = sysUserService.generateTempEmailChangeToken(verifyOldEmailCodeDTO);
        return Result.success(token);
    }

    @PreAuthorize("hasAuthority('user:email:update')")
    @PostMapping("/email/send-new-code")
    public Result sendNewEmailCode(@Valid @RequestBody SendNewEmailCodeDTO sendNewEmailCodeDTO) {
        if (sysUserService.hasEmail(sendNewEmailCodeDTO.getNewEmail())) {
            return Result.error("该新邮箱已被占用");
        }
        if (!sysUserService.verifyTempEmailChangeToken(sendNewEmailCodeDTO.getTempToken())) {
            return Result.error("验证状态已失效，请重新验证旧邮箱");
        }

        boolean success = emailService.sendEmailCode(sendNewEmailCodeDTO.getNewEmail());
        return success ? Result.success() : Result.error("验证码发送失败");
    }

    @PreAuthorize("hasAuthority('user:email:update')")
    @PutMapping("/email/update")
    public Result updateEmail(@Valid @RequestBody UserEmailUpdateDTO userEmailUpdateDTO) {
        if (!sysUserService.verifyTempEmailChangeToken(userEmailUpdateDTO.getTempToken())) {
            return Result.error("验证状态已失效，请重新验证旧邮箱");
        }
        if (!emailService.verifyCode(userEmailUpdateDTO.getNewEmail(), userEmailUpdateDTO.getNewEmailCode())) {
            return Result.error("验证码无效或已过期");
        }

        sysUserService.updateEmail(userEmailUpdateDTO);
        return Result.success();
    }
}
