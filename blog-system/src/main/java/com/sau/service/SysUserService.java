package com.sau.service;

import com.sau.pojo.DTO.RegisterDTO;
import com.sau.pojo.DTO.UserEmailUpdateDTO;
import com.sau.pojo.DTO.UserPasswordUpdateDTO;
import com.sau.pojo.DTO.UserProfilesUpdateDTO;
import com.sau.pojo.DTO.VerifyOldEmailCodeDTO;
import com.sau.pojo.entity.Result;

/**
 * 用户服务接口
 */
public interface SysUserService {

    /**
     * 用户注册
     */
    void register(RegisterDTO request);

    /**
     * 判断邮箱是否已存在
     */
    boolean hasEmail(String email);

    /**
     * 修改个人资料
     */
    Result updateProfile(UserProfilesUpdateDTO userProfilesUpdateDTO);

    /**
     * 修改密码
     */
    Result updatePassword(UserPasswordUpdateDTO userPasswordUpdateDTO);

    /**
     * 根据用户名查询邮箱
     */
    String getEmailByUsername(String username);

    /**
     * 获取当前登录用户邮箱
     */
    String getCurrentUserEmail();

    /**
     * 生成临时邮箱修改令牌
     */
    String generateTempEmailChangeToken(VerifyOldEmailCodeDTO verifyOldEmailCodeDTO);

    /**
     * 校验临时邮箱修改令牌
     */
    boolean verifyTempEmailChangeToken(String tempToken);

    /**
     * 更新邮箱
     */
    void updateEmail(UserEmailUpdateDTO userEmailUpdateDTO);
}
