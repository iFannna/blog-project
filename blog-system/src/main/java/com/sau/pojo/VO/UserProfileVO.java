package com.sau.pojo.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileVO {
    private Integer userId;
    private String username;
    private String nickname;
    private String email;
    private String phone;
    private String introduction;
    private String avatar;
}
