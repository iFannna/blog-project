package com.sau.pojo.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfilesUpdateDTO {
    /**
     * 用户ID
     */
    private Integer id;

    /**
     * 昵称
     */
    @NotBlank
    @Size(min = 1, max = 20)
    private String nickname;

    /**
     * 简介
     */
    @Size(max = 255)
    private String introduction;

    /**
     * 头像
     */
    private MultipartFile avatar;
}
