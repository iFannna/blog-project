package com.sau.service.impl;

import com.sau.constants.JwtConstants;
import com.sau.constants.RedisKeyConstants;
import com.sau.constants.SecurityConstants;
import com.sau.exception.EmailAlreadyExistsException;
import com.sau.mapper.SysRoleMapper;
import com.sau.mapper.SysUserMapper;
import com.sau.mapper.SysUserRoleMapper;
import com.sau.pojo.DTO.RegisterDTO;
import com.sau.pojo.DTO.UserEmailUpdateDTO;
import com.sau.pojo.DTO.UserPasswordUpdateDTO;
import com.sau.pojo.DTO.UserProfilesUpdateDTO;
import com.sau.pojo.DTO.VerifyOldEmailCodeDTO;
import com.sau.pojo.entity.Result;
import com.sau.pojo.entity.SysUser;
import com.sau.service.SysUserService;
import com.sau.service.third.OSSStorageService;
import com.sau.utils.JwtUtils;
import com.sau.utils.RedisUtils;
import com.sau.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户服务实现类。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl implements SysUserService {

    private static final long AVATAR_MAX_SIZE = 10 * 1024 * 1024;
    private static final String AVATAR_FILE_PATTERN = "^.*\\.(jpg|jpeg|png|gif)$";

    private final OSSStorageService ossStorageService;
    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final PasswordEncoder passwordEncoder;
    private final RedisUtils redisUtils;

    @Override
    public void register(RegisterDTO request) {
        SysUser newSysUser = buildRegisterUser(request);
        try {
            sysUserMapper.insert(newSysUser);
        } catch (DuplicateKeyException e) {
            if (e.getMessage() != null && e.getMessage().contains("email")) {
                throw new EmailAlreadyExistsException("该邮箱已被注册，请使用其他邮箱");
            }
            throw e;
        }

        bindDefaultRole(newSysUser.getId());
        redisUtils.delete(RedisKeyConstants.emailCodeKey(request.getEmail()));
        redisUtils.delete(RedisKeyConstants.emailCodeCooldownKey(request.getEmail()));
    }

    @Override
    public boolean hasEmail(String email) {
        return sysUserMapper.selectByEmail(email) != null;
    }

    @Override
    public String getEmailByUsername(String username) {
        return sysUserMapper.getEmailByUsername(username);
    }

    @Override
    public String getCurrentUserEmail() {
        return sysUserMapper.getEmailByUserId(SecurityUtils.requireCurrentUserId());
    }

    @Override
    public Result updateProfile(UserProfilesUpdateDTO userProfilesUpdateDTO) {
        Integer currentUserId = SecurityUtils.requireCurrentUserId();
        SysUser sysUser = new SysUser();
        sysUser.setId(currentUserId);
        sysUser.setNickname(userProfilesUpdateDTO.getNickname());
        sysUser.setIntroduction(userProfilesUpdateDTO.getIntroduction());

        MultipartFile avatar = userProfilesUpdateDTO.getAvatar();
        if (avatar != null) {
            Result validationResult = validateAvatar(avatar);
            if (validationResult != null) {
                return validationResult;
            }
            Result uploadResult = uploadAvatar(sysUser, avatar);
            if (uploadResult != null) {
                return uploadResult;
            }
        }

        sysUserMapper.updateProfileById(sysUser);
        return Result.success();
    }

    @Override
    public Result updatePassword(UserPasswordUpdateDTO userPasswordUpdateDTO) {
        Integer currentUserId = SecurityUtils.requireCurrentUserId();
        SysUser sysUser = sysUserMapper.selectById(currentUserId);
        if (sysUser == null) {
            log.warn("用户不存在: {}", currentUserId);
            return Result.error("用户不存在");
        }
        if (!passwordEncoder.matches(userPasswordUpdateDTO.getOldPassword(), sysUser.getPassword())) {
            log.warn("密码错误: {}", currentUserId);
            return Result.error("密码错误");
        }

        SysUser updateUser = new SysUser();
        updateUser.setId(currentUserId);
        updateUser.setPassword(passwordEncoder.encode(userPasswordUpdateDTO.getNewPassword()));
        sysUserMapper.updatePasswordById(updateUser);
        return Result.success();
    }

    @Override
    public String generateTempEmailChangeToken(VerifyOldEmailCodeDTO verifyOldEmailCodeDTO) {
        Integer currentUserId = SecurityUtils.requireCurrentUserId();
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtConstants.USER_ID, currentUserId);
        claims.put("oldEmail", verifyOldEmailCodeDTO.getOldEmail());
        String tempToken = JwtUtils.generateToken(claims, JwtConstants.TEMP_TOKEN_EXPIRATION);
        redisUtils.set(RedisKeyConstants.tempEmailChangeKey(tempToken), claims, JwtConstants.TEMP_TOKEN_EXPIRATION);
        return tempToken;
    }

    @Override
    public boolean verifyTempEmailChangeToken(String tempToken) {
        Object claims = redisUtils.get(RedisKeyConstants.tempEmailChangeKey(tempToken));
        if (!(claims instanceof Map<?, ?> claimMap)) {
            log.warn("临时邮箱修改 token 不存在: {}", tempToken);
            return false;
        }
        Object userId = claimMap.get(JwtConstants.USER_ID);
        return userId != null && userId.toString().equals(SecurityUtils.requireCurrentUserId().toString());
    }

    @Override
    public void updateEmail(UserEmailUpdateDTO userEmailUpdateDTO) {
        SysUser sysUser = new SysUser();
        sysUser.setId(SecurityUtils.requireCurrentUserId());
        sysUser.setEmail(userEmailUpdateDTO.getNewEmail());
        sysUserMapper.updateEmailById(sysUser);
    }

    private SysUser buildRegisterUser(RegisterDTO request) {
        SysUser sysUser = new SysUser();
        sysUser.setNickname(request.getUsername());
        sysUser.setUsername(request.getUsername());
        sysUser.setEmail(request.getEmail());
        sysUser.setPassword(passwordEncoder.encode(request.getPassword()));
        sysUser.setStatus(1);
        return sysUser;
    }

    private void bindDefaultRole(Integer userId) {
        Integer roleId = sysRoleMapper.selectIdByRoleCode(SecurityConstants.ROLE_USER);
        if (roleId == null) {
            throw new IllegalStateException("默认角色 ROLE_USER 不存在，请先初始化 RBAC 数据");
        }
        sysUserRoleMapper.insert(userId, roleId);
    }

    private Result validateAvatar(MultipartFile avatar) {
        if (avatar.getSize() > AVATAR_MAX_SIZE) {
            log.error("头像文件过大，文件大小:{}KB，最大允许:{}KB", avatar.getSize() / 1024, AVATAR_MAX_SIZE / 1024);
            return Result.error("头像文件过大，请上传小于 10MB 的图片");
        }

        String originalFilename = avatar.getOriginalFilename();
        if (originalFilename == null || !originalFilename.matches(AVATAR_FILE_PATTERN)) {
            log.error("头像文件格式不支持，文件名:{}", originalFilename);
            return Result.error("文件格式不支持，请上传 jpg、jpeg、png 或 gif 格式图片");
        }
        return null;
    }

    private Result uploadAvatar(SysUser sysUser, MultipartFile avatar) {
        try {
            String avatarUrl = ossStorageService.upload(avatar.getBytes(), avatar.getOriginalFilename());
            log.info("用户头像上传到 OSS 成功，URL:{}", avatarUrl);
            sysUser.setAvatar(avatarUrl);
            return null;
        } catch (IOException e) {
            log.error("读取头像文件失败", e);
            return Result.error("读取头像文件失败");
        } catch (Exception e) {
            log.error("头像上传失败", e);
            return Result.error("头像上传失败");
        }
    }
}