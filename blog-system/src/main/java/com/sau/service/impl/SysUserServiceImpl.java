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
 * 用户服务实现类
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
        // 组装注册用户并写入数据库
        SysUser newSysUser = buildRegisterUser(request);
        try {
            sysUserMapper.insert(newSysUser);
        } catch (DuplicateKeyException e) {
            if (e.getMessage() != null && e.getMessage().contains("email")) {
                throw new EmailAlreadyExistsException("该邮箱已被注册，请使用其他邮箱");
            }
            throw e;
        }

        // 绑定默认角色，并清理本次注册使用的邮箱验证码缓存
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
        // 先封装需要更新的用户基础资料
        Integer currentUserId = SecurityUtils.requireCurrentUserId();
        SysUser sysUser = new SysUser();
        sysUser.setId(currentUserId);
        sysUser.setNickname(userProfilesUpdateDTO.getNickname());
        sysUser.setIntroduction(userProfilesUpdateDTO.getIntroduction());

        MultipartFile avatar = userProfilesUpdateDTO.getAvatar();
        if (avatar != null) {
            // 上传头像前先校验文件大小和格式
            Result validationResult = validateAvatar(avatar);
            if (validationResult != null) {
                return validationResult;
            }
            // 校验通过后上传头像，并回填头像地址
            Result uploadResult = uploadAvatar(sysUser, avatar);
            if (uploadResult != null) {
                return uploadResult;
            }
        }

        // 持久化最新个人资料
        sysUserMapper.updateProfileById(sysUser);
        return Result.success();
    }

    @Override
    public Result updatePassword(UserPasswordUpdateDTO userPasswordUpdateDTO) {
        // 先加载当前用户，校验旧密码是否正确
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

        // 重新加密新密码并更新到数据库
        SysUser updateUser = new SysUser();
        updateUser.setId(currentUserId);
        updateUser.setPassword(passwordEncoder.encode(userPasswordUpdateDTO.getNewPassword()));
        sysUserMapper.updatePasswordById(updateUser);
        return Result.success();
    }

    @Override
    public String generateTempEmailChangeToken(VerifyOldEmailCodeDTO verifyOldEmailCodeDTO) {
        // 生成包含当前用户和旧邮箱信息的临时令牌
        Integer currentUserId = SecurityUtils.requireCurrentUserId();
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtConstants.USER_ID, currentUserId);
        claims.put("oldEmail", verifyOldEmailCodeDTO.getOldEmail());
        String tempToken = JwtUtils.generateToken(claims, JwtConstants.TEMP_TOKEN_EXPIRATION);
        // 将临时令牌声明缓存到 Redis，便于后续校验
        redisUtils.set(RedisKeyConstants.tempEmailChangeKey(tempToken), claims, JwtConstants.TEMP_TOKEN_EXPIRATION);
        return tempToken;
    }

    @Override
    public boolean verifyTempEmailChangeToken(String tempToken) {
        // 从 Redis 中读取临时令牌声明
        Object claims = redisUtils.get(RedisKeyConstants.tempEmailChangeKey(tempToken));
        if (!(claims instanceof Map<?, ?> claimMap)) {
            log.warn("临时邮箱修改 token 不存在: {}", tempToken);
            return false;
        }
        // 校验令牌中的用户是否与当前登录用户一致
        Object userId = claimMap.get(JwtConstants.USER_ID);
        return userId != null && userId.toString().equals(SecurityUtils.requireCurrentUserId().toString());
    }

    @Override
    public void updateEmail(UserEmailUpdateDTO userEmailUpdateDTO) {
        // 封装当前用户的新邮箱并更新数据库
        SysUser sysUser = new SysUser();
        sysUser.setId(SecurityUtils.requireCurrentUserId());
        sysUser.setEmail(userEmailUpdateDTO.getNewEmail());
        sysUserMapper.updateEmailById(sysUser);
    }

    private SysUser buildRegisterUser(RegisterDTO request) {
        // 初始化注册用户的基础字段
        SysUser sysUser = new SysUser();
        sysUser.setNickname(request.getUsername());
        sysUser.setUsername(request.getUsername());
        sysUser.setEmail(request.getEmail());
        sysUser.setPassword(passwordEncoder.encode(request.getPassword()));
        sysUser.setStatus(1);
        return sysUser;
    }

    private void bindDefaultRole(Integer userId) {
        // 查询默认用户角色并建立用户角色关联
        Integer roleId = sysRoleMapper.selectIdByRoleCode(SecurityConstants.ROLE_USER);
        if (roleId == null) {
            throw new IllegalStateException("默认角色 ROLE_USER 不存在，请先初始化 RBAC 数据");
        }
        sysUserRoleMapper.insert(userId, roleId);
    }

    private Result validateAvatar(MultipartFile avatar) {
        // 校验头像大小是否超过限制
        if (avatar.getSize() > AVATAR_MAX_SIZE) {
            log.error("头像文件过大，文件大小:{}KB，最大允许:{}KB", avatar.getSize() / 1024, AVATAR_MAX_SIZE / 1024);
            return Result.error("头像文件过大，请上传小于 10MB 的图片");
        }

        // 校验头像文件扩展名是否符合要求
        String originalFilename = avatar.getOriginalFilename();
        if (originalFilename == null || !originalFilename.matches(AVATAR_FILE_PATTERN)) {
            log.error("头像文件格式不支持，文件名:{}", originalFilename);
            return Result.error("文件格式不支持，请上传 jpg、jpeg、png 或 gif 格式图片");
        }
        return null;
    }

    private Result uploadAvatar(SysUser sysUser, MultipartFile avatar) {
        try {
            // 上传头像到 OSS，并回填用户头像地址
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
