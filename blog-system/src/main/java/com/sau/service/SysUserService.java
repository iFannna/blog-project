package com.sau.service;

import com.sau.pojo.DTO.*;
import com.sau.pojo.entity.Result;


public interface SysUserService {

    /**
     * 注册
     */
    void register(RegisterDTO request);

    /**
     * 邮箱是否存在
     */
    boolean hasEmail(String email);

    /**
     * 修改用户信息
     */
    Result updateProfiles(UserProfilesUpdateDTO userProfilesUpdateDTO);

    /**
     * 修改密码
     */
    Result updatePassword(UserPasswordUpdateDTO userPasswordUpdateDTO);

    /**
     * 根据用户名获取邮箱
     */
    String getEmailByUsername(String username);

    /**
     * 生成临时邮箱修改令牌
     */
    String generateTempEmailChangeToken(VerifyOldEmailCodeDTO verifyOldEmailCodeDTO);

    /**
     * 验证临时邮箱修改令牌
     */
    boolean verifyTempEmailChangeToken(String tempToken);

    /**
     * 修改邮箱
     */
    void updateEmail(UserEmailUpdateDTO userEmailUpdateDTO);
}
