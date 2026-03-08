-- Blog-system RBAC initialization script
-- Current database assumption:
--   1. sys_user already contains business user data
--   2. sys_role / sys_permission / sys_user_role / sys_role_permission are empty or need re-initialization
--   3. Only two roles are required: ROLE_ADMIN and ROLE_USER
--
-- Suggested execution order:
--   1. Execute this script after base tables are created
--   2. If your administrator username is not `admin`, replace it in the ROLE_ADMIN binding SQL below

SET NAMES utf8mb4;

-- Optional schema hardening: keep username unique for stable login identity.
-- ALTER TABLE sys_user ADD CONSTRAINT uk_sys_user_username UNIQUE (username);

-- Compatibility migration: unify old add-style permission codes to create-style permission codes.
UPDATE sys_permission SET perm_code = 'article:create' WHERE perm_code = 'article:add';
UPDATE sys_permission SET perm_code = 'category:create' WHERE perm_code = 'category:add';
UPDATE sys_permission SET perm_code = 'tag:create' WHERE perm_code = 'tag:add';
UPDATE sys_permission SET perm_code = 'comment:create' WHERE perm_code = 'comment:add';
UPDATE sys_permission SET perm_code = 'comment:reply:create' WHERE perm_code = 'comment:reply:add';
UPDATE sys_permission SET perm_code = 'message:create' WHERE perm_code = 'message:add';

-- =========================
-- 1. Initialize roles
-- =========================
INSERT INTO sys_role (role_name, role_code, status, description, create_time, update_time)
SELECT '系统管理员', 'ROLE_ADMIN', 1, '拥有全部管理权限，并在数据归属校验时直接放行', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_code = 'ROLE_ADMIN');

INSERT INTO sys_role (role_name, role_code, status, description, create_time, update_time)
SELECT '普通用户', 'ROLE_USER', 1, '注册用户默认角色，仅保留账号自助能力', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_code = 'ROLE_USER');

-- ==============================
-- 2. Initialize permissions
-- ==============================
-- Permissions currently enforced by the code.
INSERT INTO sys_permission (perm_name, perm_code, resource_type, url, method, parent_id, status, create_time, update_time)
SELECT '修改个人资料', 'user:profile:update', 'API', '/user/profiles', 'PUT', 0, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE perm_code = 'user:profile:update');

INSERT INTO sys_permission (perm_name, perm_code, resource_type, url, method, parent_id, status, create_time, update_time)
SELECT '修改密码', 'user:password:update', 'API', '/user/password', 'PUT', 0, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE perm_code = 'user:password:update');

INSERT INTO sys_permission (perm_name, perm_code, resource_type, url, method, parent_id, status, create_time, update_time)
SELECT '修改邮箱', 'user:email:update', 'API', '/user/email/*', 'POST/PUT', 0, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE perm_code = 'user:email:update');

INSERT INTO sys_permission (perm_name, perm_code, resource_type, url, method, parent_id, status, create_time, update_time)
SELECT '创建文章', 'article:create', 'API', '/article', 'POST', 0, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE perm_code = 'article:create');

INSERT INTO sys_permission (perm_name, perm_code, resource_type, url, method, parent_id, status, create_time, update_time)
SELECT '删除文章', 'article:delete', 'API', '/article', 'DELETE', 0, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE perm_code = 'article:delete');

INSERT INTO sys_permission (perm_name, perm_code, resource_type, url, method, parent_id, status, create_time, update_time)
SELECT '删除评论', 'comment:delete', 'API', '/comment/{id}', 'DELETE', 0, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE perm_code = 'comment:delete');

INSERT INTO sys_permission (perm_name, perm_code, resource_type, url, method, parent_id, status, create_time, update_time)
SELECT '创建分类', 'category:create', 'API', '/category', 'POST', 0, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE perm_code = 'category:create');

INSERT INTO sys_permission (perm_name, perm_code, resource_type, url, method, parent_id, status, create_time, update_time)
SELECT '删除分类', 'category:delete', 'API', '/category/{id}', 'DELETE', 0, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE perm_code = 'category:delete');

INSERT INTO sys_permission (perm_name, perm_code, resource_type, url, method, parent_id, status, create_time, update_time)
SELECT '创建标签', 'tag:create', 'API', '/tag', 'POST', 0, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE perm_code = 'tag:create');

INSERT INTO sys_permission (perm_name, perm_code, resource_type, url, method, parent_id, status, create_time, update_time)
SELECT '删除标签', 'tag:delete', 'API', '/tag/{id}', 'DELETE', 0, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE perm_code = 'tag:delete');

-- Reserved extension permissions for future interactive features.
INSERT INTO sys_permission (perm_name, perm_code, resource_type, url, method, parent_id, status, create_time, update_time)
SELECT '创建评论', 'comment:create', 'API', '/comment', 'POST', 0, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE perm_code = 'comment:create');

INSERT INTO sys_permission (perm_name, perm_code, resource_type, url, method, parent_id, status, create_time, update_time)
SELECT '创建评论回复', 'comment:reply:create', 'API', '/comment/reply', 'POST', 0, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE perm_code = 'comment:reply:create');

INSERT INTO sys_permission (perm_name, perm_code, resource_type, url, method, parent_id, status, create_time, update_time)
SELECT '点赞评论', 'comment:like', 'API', '/comment/like', 'POST', 0, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE perm_code = 'comment:like');

INSERT INTO sys_permission (perm_name, perm_code, resource_type, url, method, parent_id, status, create_time, update_time)
SELECT '创建留言', 'message:create', 'API', '/message', 'POST', 0, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE perm_code = 'message:create');

INSERT INTO sys_permission (perm_name, perm_code, resource_type, url, method, parent_id, status, create_time, update_time)
SELECT '点赞留言', 'message:like', 'API', '/message/like', 'POST', 0, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE perm_code = 'message:like');

-- ==================================
-- 3. Initialize role-permission links
-- ==================================
-- ROLE_ADMIN gets all current management permissions.
INSERT INTO sys_role_permission (role_id, perm_id, create_time, update_time)
SELECT r.id, p.id, NOW(), NOW()
FROM sys_role r
JOIN sys_permission p ON p.perm_code IN (
    'user:profile:update',
    'user:password:update',
    'user:email:update',
    'article:create',
    'article:delete',
    'comment:delete',
    'category:create',
    'category:delete',
    'tag:create',
    'tag:delete'
)
WHERE r.role_code = 'ROLE_ADMIN'
  AND NOT EXISTS (
      SELECT 1 FROM sys_role_permission rp WHERE rp.role_id = r.id AND rp.perm_id = p.id
  );

-- ROLE_USER only gets self-service permissions in the current version.
INSERT INTO sys_role_permission (role_id, perm_id, create_time, update_time)
SELECT r.id, p.id, NOW(), NOW()
FROM sys_role r
JOIN sys_permission p ON p.perm_code IN (
    'user:profile:update',
    'user:password:update',
    'user:email:update'
)
WHERE r.role_code = 'ROLE_USER'
  AND NOT EXISTS (
      SELECT 1 FROM sys_role_permission rp WHERE rp.role_id = r.id AND rp.perm_id = p.id
  );

-- ==================================
-- 4. Initialize user-role links
-- ==================================
-- Bind administrator role to the existing admin account.
-- If your administrator username is not `admin`, replace it before executing.
INSERT INTO sys_user_role (user_id, role_id, create_time, update_time)
SELECT u.id, r.id, NOW(), NOW()
FROM sys_user u
JOIN sys_role r ON r.role_code = 'ROLE_ADMIN'
WHERE u.username = 'admin'
  AND NOT EXISTS (
      SELECT 1 FROM sys_user_role ur WHERE ur.user_id = u.id AND ur.role_id = r.id
  );

-- Assign ROLE_USER to all users that still do not have any role.
INSERT INTO sys_user_role (user_id, role_id, create_time, update_time)
SELECT u.id, r.id, NOW(), NOW()
FROM sys_user u
JOIN sys_role r ON r.role_code = 'ROLE_USER'
WHERE NOT EXISTS (
    SELECT 1 FROM sys_user_role ur WHERE ur.user_id = u.id
);
