package com.sau.security;

import com.sau.constants.SecurityConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 当前认证用户快照
 * 保存登录用户的基础身份、角色码、权限码以及管理员标记
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthUser implements Serializable {
    /** 用户 ID */
    private Integer userId;
    /** 用户名 */
    private String username;
    /** 角色编码集合 */
    @Builder.Default
    private Set<String> roleCodes = Collections.emptySet();
    /** 权限编码集合 */
    @Builder.Default
    private Set<String> permissionCodes = Collections.emptySet();
    /** 是否管理员 */
    private boolean admin;

    /**
     * 将角色和权限统一转换成 Spring Security 可识别的授权集合
     */
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<String> authorities = new LinkedHashSet<>();
        authorities.addAll(roleCodes);
        authorities.addAll(permissionCodes);
        return authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * 仅根据用户 ID 构造一个最小身份对象
     */
    public static AuthUser of(Integer userId) {
        return AuthUser.builder()
                .userId(userId)
                .roleCodes(Collections.emptySet())
                .permissionCodes(Collections.emptySet())
                .admin(false)
                .build();
    }

    /**
     * 根据用户、角色、权限构造完整身份对象
     */
    public static AuthUser of(Integer userId, String username, List<String> roleCodes, List<String> permissionCodes) {
        Set<String> safeRoles = roleCodes == null ? Collections.emptySet() : new LinkedHashSet<>(roleCodes);
        Set<String> safePermissions = permissionCodes == null ? Collections.emptySet() : new LinkedHashSet<>(permissionCodes);
        return AuthUser.builder()
                .userId(userId)
                .username(username)
                .roleCodes(Collections.unmodifiableSet(safeRoles))
                .permissionCodes(Collections.unmodifiableSet(safePermissions))
                .admin(safeRoles.contains(SecurityConstants.ROLE_ADMIN))
                .build();
    }
}
