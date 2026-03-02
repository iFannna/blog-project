package com.sau.service.impl;

import com.sau.constants.JwtConstants;
import com.sau.exception.EmailAlreadyExistsException;
import com.sau.mapper.UserMapper;
import com.sau.pojo.DTO.*;
import com.sau.pojo.entity.Result;
import com.sau.pojo.entity.User;
import com.sau.service.UserService;
import com.sau.service.third.OSSStorageService;
import com.sau.utils.JwtUtils;
import com.sau.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private OSSStorageService ossStorageService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RedisUtils redisUtils;

    /**
     * 注册功能
     */
    @Override
    public void register(RegisterDTO request) {

        User newUser = new User();
        newUser.setNickname(request.getUsername());
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());
        // 用PasswordEncoder加密密码
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setCreateTime(LocalDateTime.now());
        newUser.setStatus(1);

        try {
            userMapper.insert(newUser);
        } catch (DuplicateKeyException e) {
            if (e.getMessage().contains("user.email")) {
                throw new EmailAlreadyExistsException("该邮箱已被注册，请使用其他邮箱");
            }
            throw e;
        }
        log.info("用户注册成功，账号：{}", request.getUsername());
        // 注册成功删除Redis中的邮箱注册码
        redisUtils.delete("register:code:" + request.getEmail());
    }

    /**
     * 判断邮箱是否存在
     */
    @Override
    public boolean hasEmail(String email) {
        User user = userMapper.selectByEmail(email);
        return user != null;
    }

    /**
     * 根据用户名获取邮箱
     */
    @Override
    public String getEmailByUsername(String username) {
        return userMapper.getEmailByUsername(username);
    }

    /**
     * 修改用户资料
     */
    @Override
    public boolean updateProfiles(UserProfilesUpdateDTO userProfilesUpdateDTO) {
        User user = new User();
        user.setId(userProfilesUpdateDTO.getId());
        user.setNickname(userProfilesUpdateDTO.getNickname());
        user.setIntroduction(userProfilesUpdateDTO.getIntroduction());

        // 判断是否有上传头像
        if (userProfilesUpdateDTO.getAvatar() != null){
            MultipartFile avatar = userProfilesUpdateDTO.getAvatar();
            // 1. 校验文件大小
            long fileSize = avatar.getSize();
            long maxSize = 10 * 1024 * 1024; // 10MB
            if (fileSize > maxSize) {
                log.error("头像文件过大，文件大小：{}KB，最大允许：{}KB", fileSize/1024, maxSize/1024);
                return false;
            }

            // 2. 校验文件类型
            String originalFilename = avatar.getOriginalFilename();
            if (originalFilename == null || !originalFilename.matches("^.*\\.(jpg|jpeg|png|gif)$")) {
                log.error("头像文件格式不支持，文件名：{}", originalFilename);
                return false;
            }

            try {
                // 3. 获取文件字节数组
                byte[] content = avatar.getBytes();

                // 4. 调用OSS服务上传文件，获取访问URL
                String avatarUrl = ossStorageService.upload(content, originalFilename);
                log.info("用户头像上传到OSS成功，URL：{}", avatarUrl);

                // 5. 更新用户头像URL
                user.setAvatar(avatarUrl);

            } catch (IOException e) {
                log.error("读取头像文件失败", e);
                return false;
            } catch (Exception e) {
                log.error("头像上传/更新失败", e);
                return false;
            }
        }

        // 最后更新用户资料
        userMapper.updateProfiles(user);
        return true;
    }

    /**
     * 修改用户密码
     */
    @Override
    public Result updatePassword(UserPasswordUpdateDTO userPasswordUpdateDTO) {
        // 先根据DTO中的用户名与密码查询用户
        String username = userPasswordUpdateDTO.getUsername();
        String oldPassword = userPasswordUpdateDTO.getOldPassword();
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            log.warn("用户不存在：{}", username);
            return Result.error("用户不存在");
        }
        // 用PasswordEncoder验证明文密码与数据库中的加密密码是否匹配
        boolean matches = passwordEncoder.matches(oldPassword, user.getPassword());
        if (!matches) {
            log.warn("密码错误：{}", username);
            return Result.error("密码错误");
        }

        user.setPassword(userPasswordUpdateDTO.getNewPassword());
        userMapper.updatePassword(user);
        log.info("用户密码修改成功，用户名：{}", username);
        return Result.success();
    }



    /**
     * 生成临时邮箱修改token
     */
    @Override
    public String generateTempEmailChangeToken(VerifyOldEmailCodeDTO verifyOldEmailCodeDTO) {
        // 生成临时token
        Map<String, Object> claims = new HashMap<>();
        claims.put("oldEmail", verifyOldEmailCodeDTO.getOldEmail());
        claims.put("oldEmailCode", verifyOldEmailCodeDTO.getOldEmailCode());
        String tempToken = JwtUtils.generateToken(claims, JwtConstants.TEMP_TOKEN_EXPIRATION);
        log.info("生成临时邮箱修改token：{}", tempToken);
        // 将token存入Redis
        redisUtils.set("temp:email:change:" + tempToken, claims, JwtConstants.TEMP_TOKEN_EXPIRATION);
        return tempToken;
    }

    /**
     * 验证临时邮箱修改token
     */
    @Override
    public boolean verifyTempEmailChangeToken(String tempToken) {
        // 获取token对应的claims
        Object claims = redisUtils.get("temp:email:change:" + tempToken);
        if (claims == null) {
            log.warn("临时邮箱修改token不存在：{}", tempToken);
            return false;
        }
        return true;
    }

    /**
     * 修改用户邮箱
     */
    @Override
    public void updateEmail(UserEmailUpdateDTO userEmailUpdateDTO) {
        User user = new User();
        user.setUsername(userEmailUpdateDTO.getUsername());
        user.setEmail(userEmailUpdateDTO.getNewEmail());

        userMapper.updateEmail(user);
    }


}