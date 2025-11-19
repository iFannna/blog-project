package com.sau.service.impl;

import com.sau.exception.EmailAlreadyExistsException;
import com.sau.mapper.UserMapper;
import com.sau.pojo.DTO.RegisterDTO;
import com.sau.pojo.entity.User;
import com.sau.service.UserService;
import com.sau.service.third.EmailService;
import com.sau.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private RedisUtils redisUtils;

    /**
     * 注册功能
     */
    @Override
    public void register(RegisterDTO request) {

        User newUser = new User();
        newUser.setName(request.getUsername());
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
        // 注册成功删除Redis中的captchaVerifyParam
        redisUtils.delete(request.getCaptchaParams());
    }

    /**
     * 判断用户名是否存在
     */
    @Override
    public boolean hasUsername(String username) {
        User user = userMapper.selectByUsername(username);
        return user != null;
    }

    /**
     * 判断邮箱是否存在
     */
    @Override
    public boolean hasEmail(String email) {
        User user = userMapper.selectByEmail(email);
        return user != null;
    }
}