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
    @NotBlank
    @Size(min = 1, max = 20)
    private String nickname;

    @Size(max = 255)
    private String introduction;

    @Size(max = 20)
    private String phone;

    private MultipartFile avatar;
}
