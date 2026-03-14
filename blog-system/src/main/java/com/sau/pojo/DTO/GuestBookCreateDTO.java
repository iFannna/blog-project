package com.sau.pojo.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuestBookCreateDTO {
    @NotBlank(message = "留言内容不能为空")
    @Size(max = 1000, message = "留言内容不能超过 1000 个字符")
    private String content;

    private Boolean anonymous;
}
