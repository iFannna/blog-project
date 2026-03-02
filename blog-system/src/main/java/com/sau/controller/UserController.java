package com.sau.controller;

import com.sau.pojo.DTO.*;
import com.sau.pojo.VO.CaptchaVerifyVO;
import com.sau.pojo.entity.Result;
import com.sau.service.UserService;
import com.sau.service.third.CaptchaService;
import com.sau.service.third.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * 用户相关接口
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private CaptchaService captchaService;

    /**
     * 修改个人资料
     */
    @PutMapping(value ="/profiles", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result updateProfiles(@ModelAttribute UserProfilesUpdateDTO userProfilesUpdateDTO) {
        log.info("修改头像");
        boolean result = userService.updateProfiles(userProfilesUpdateDTO);
        if (!result) {
            return Result.error("更新失败");
        }
        return Result.success();
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    public Result updatePassword(@RequestBody UserPasswordUpdateDTO userPasswordUpdateDTO) {
        return userService.updatePassword(userPasswordUpdateDTO);
    }

    /**
     * 发送旧邮箱验证码
     */
    @PostMapping("/email/send-old-code")
    public Result sendOldEmailCode(@RequestBody SendOldEmailCodeDTO sendOldEmailCodeDTO) {

        // 安全校验
        CaptchaVerifyVO captchaResult = captchaService.verifyCaptcha(sendOldEmailCodeDTO.getCaptchaParam());
        if (!captchaResult.isSuccess()) {
            return Result.error("安全验证失败，请重试");
        }
        // 根据用户名查询邮箱
        String username = sendOldEmailCodeDTO.getUsername();
        String dbEmail = userService.getEmailByUsername(username);
        // 判断旧邮箱是否正确
        if (!dbEmail.equals(sendOldEmailCodeDTO.getOldEmail())) {
            return Result.error("邮箱错误");
        }
        // 向旧邮箱发送验证码
        boolean success = emailService.sendEmailCode(sendOldEmailCodeDTO.getOldEmail());
        return success ?
                Result.success() :
                Result.error("验证码发送失败，请稍后重试");
    }

    /**
     * 验证旧邮箱验证码
     */
    @PostMapping("/email/verify-old-code")
    public Result verifyOldEmailCode(@RequestBody VerifyOldEmailCodeDTO verifyOldEmailCodeDTO) {

        if (!emailService.verifyCode(verifyOldEmailCodeDTO.getOldEmailCode(), verifyOldEmailCodeDTO.getOldEmailCode())){
            return Result.error("验证码错误或已失效");
        }
        // 生成临时换绑令牌
        String token = userService.generateTempEmailChangeToken(verifyOldEmailCodeDTO);

        return Result.success(token);
    }

    /**
     * 发送新邮箱验证码
     */
    @PostMapping("/email/send-new-code")
    public Result sendNewEmailCode(@RequestBody SendNewEmailCodeDTO sendNewEmailCodeDTO) {
        // 判断新邮箱是否已存在
        if (userService.hasEmail(sendNewEmailCodeDTO.getNewEmail())) {
            return Result.error("该邮箱已被使用");
        }
        // 验证临时换绑令牌
        if (!userService.verifyTempEmailChangeToken(sendNewEmailCodeDTO.getTempToken())) {
            return Result.error("令牌已失效");
        }
        // 向新邮箱发送验证码
        boolean success = emailService.sendEmailCode(sendNewEmailCodeDTO.getNewEmail());
        return success ?
                Result.success() :
                Result.error("验证码发送失败，请稍后重试");
    }


    /**
     * 修改邮箱
     */
    @PutMapping("/email/update")
    public Result updateEmail(@RequestBody UserEmailUpdateDTO userEmailUpdateDTO) {
        // 验证临时换绑令牌
        if (!userService.verifyTempEmailChangeToken(userEmailUpdateDTO.getTempToken())) {
            return Result.error("令牌已失效");
        }
        // 验证新邮箱验证码
        if (!emailService.verifyCode(userEmailUpdateDTO.getNewEmail(), userEmailUpdateDTO.getNewEmailCode())){
            return Result.error("验证码错误或已失效");
        }

        // 修改邮箱
        userService.updateEmail(userEmailUpdateDTO);
        return Result.success();
    }

}
