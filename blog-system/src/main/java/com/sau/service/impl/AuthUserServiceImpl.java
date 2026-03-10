package com.sau.service.impl;

import com.sau.mapper.SysPermissionMapper;
import com.sau.mapper.SysRoleMapper;
import com.sau.mapper.SysUserMapper;
import com.sau.pojo.entity.SysUser;
import com.sau.security.AuthUser;
import com.sau.service.AuthUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 认证用户加载服务实现
 */
@Service
@RequiredArgsConstructor
public class AuthUserServiceImpl implements AuthUserService {

    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysPermissionMapper sysPermissionMapper;

    /**
     * 根据用户 ID 加载已启用用户的角色与权限信息
     */
    @Override
    public AuthUser loadAuthUser(Integer userId) {
        // 先确认用户存在且处于启用状态
        SysUser sysUser = sysUserMapper.selectById(userId);
        if (sysUser == null || sysUser.getStatus() == null || sysUser.getStatus() != 1) {
            return null;
        }
        // 查询当前用户拥有的角色码和权限码
        List<String> roleCodes = sysRoleMapper.selectRoleCodesByUserId(userId);
        List<String> permissionCodes = sysPermissionMapper.selectPermissionCodesByUserId(userId);
        // 组装 Spring Security 使用的认证用户对象
        return AuthUser.of(userId, sysUser.getUsername(), roleCodes, permissionCodes);
    }
}
