package com.sau.service;

import com.sau.pojo.DTO.RegisterDTO;
import com.sau.pojo.DTO.UserEmailUpdateDTO;
import com.sau.pojo.DTO.UserPasswordUpdateDTO;
import com.sau.pojo.DTO.UserProfilesUpdateDTO;
import com.sau.pojo.DTO.VerifyOldEmailCodeDTO;
import com.sau.pojo.VO.UserProfileVO;
import com.sau.pojo.entity.Result;

public interface SysUserService {

    void register(RegisterDTO request);

    boolean hasEmail(String email);

    UserProfileVO getCurrentProfile();

    Result updateProfile(UserProfilesUpdateDTO userProfilesUpdateDTO);

    Result updatePassword(UserPasswordUpdateDTO userPasswordUpdateDTO);

    String getEmailByUsername(String username);

    String getCurrentUserEmail();

    String generateTempEmailChangeToken(VerifyOldEmailCodeDTO verifyOldEmailCodeDTO);

    boolean verifyTempEmailChangeToken(String tempToken);

    void updateEmail(UserEmailUpdateDTO userEmailUpdateDTO);
}
