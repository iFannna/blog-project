package com.sau.pojo.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuestBookQueryDTO {
    private Integer page = 1;
    private Integer pageSize = 6;
}
