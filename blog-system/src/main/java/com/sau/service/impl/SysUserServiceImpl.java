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
import com.sau.pojo.VO.UserProfileVO;
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
                throw new EmailAlreadyExistsException("该邮箱已被注册");
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
    public UserProfileVO getCurrentProfile() {
        SysUser sysUser = sysUserMapper.selectById(SecurityUtils.requireCurrentUserId());
        return toUserProfileVO(sysUser);
    }

    @Override
    public Result updateProfile(UserProfilesUpdateDTO userProfilesUpdateDTO) {
        Integer currentUserId = SecurityUtils.requireCurrentUserId();
        SysUser currentUser = sysUserMapper.selectById(currentUserId);
        if (currentUser == null) {
            log.warn("User not found: {}", currentUserId);
            return Result.error("用户不存在");
        }

        SysUser updateUser = new SysUser();
        updateUser.setId(currentUserId);
        updateUser.setNickname(userProfilesUpdateDTO.getNickname());
        updateUser.setIntroduction(userProfilesUpdateDTO.getIntroduction());
        updateUser.setPhone(userProfilesUpdateDTO.getPhone());
        updateUser.setAvatar(currentUser.getAvatar());

        MultipartFile avatar = userProfilesUpdateDTO.getAvatar();
        if (avatar != null) {
            Result validationResult = validateAvatar(avatar);
            if (validationResult != null) {
                return validationResult;
            }

            Result uploadResult = uploadAvatar(updateUser, avatar);
            if (uploadResult != null) {
                return uploadResult;
            }
        }

        sysUserMapper.updateProfileById(updateUser);
        SysUser latestUser = sysUserMapper.selectById(currentUserId);
        return Result.success(toUserProfileVO(latestUser));
    }

    @Override
    public Result updatePassword(UserPasswordUpdateDTO userPasswordUpdateDTO) {
        Integer currentUserId = SecurityUtils.requireCurrentUserId();
        SysUser sysUser = sysUserMapper.selectById(currentUserId);
        if (sysUser == null) {
            log.warn("User not found: {}", currentUserId);
            return Result.error("用户不存在");
        }
        if (!passwordEncoder.matches(userPasswordUpdateDTO.getOldPassword(), sysUser.getPassword())) {
            log.warn("Password mismatch: {}", currentUserId);
            return Result.error("当前密码错误");
        }

        SysUser updateUser = new SysUser();
        updateUser.setId(currentUserId);
        updateUser.setPassword(passwordEncoder.encode(userPasswordUpdateDTO.getNewPassword()));
        sysUserMapper.updatePasswordById(updateUser);
        return Result.success();
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
            log.warn("Temp email token not found: {}", tempToken);
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
            throw new IllegalStateException("默认角色 ROLE_USER 不存在");
        }
        sysUserRoleMapper.insert(userId, roleId);
    }

    private Result validateAvatar(MultipartFile avatar) {
        if (avatar.getSize() > AVATAR_MAX_SIZE) {
            log.error("Avatar file is too large: {}KB", avatar.getSize() / 1024);
            return Result.error("头像大小不能超过 10MB");
        }

        String originalFilename = avatar.getOriginalFilename();
        if (originalFilename == null || !originalFilename.matches(AVATAR_FILE_PATTERN)) {
            log.error("Unsupported avatar file type: {}", originalFilename);
            return Result.error("头像格式仅支持 jpg、jpeg、png 或 gif");
        }

        return null;
    }

    private Result uploadAvatar(SysUser sysUser, MultipartFile avatar) {
        try {
            String avatarUrl = ossStorageService.upload(avatar.getBytes(), avatar.getOriginalFilename());
            log.info("Avatar uploaded successfully: {}", avatarUrl);
            sysUser.setAvatar(avatarUrl);
            return null;
        } catch (IOException e) {
            log.error("Failed to read avatar file", e);
            return Result.error("读取头像文件失败");
        } catch (Exception e) {
            log.error("Failed to upload avatar", e);
            return Result.error("上传头像失败");
        }
    }

    private UserProfileVO toUserProfileVO(SysUser sysUser) {
        if (sysUser == null) {
            return null;
        }

        return new UserProfileVO(
                sysUser.getId(),
                sysUser.getUsername(),
                sysUser.getNickname(),
                sysUser.getEmail(),
                sysUser.getPhone(),
                sysUser.getIntroduction(),
                sysUser.getAvatar()
        );
    }
}
