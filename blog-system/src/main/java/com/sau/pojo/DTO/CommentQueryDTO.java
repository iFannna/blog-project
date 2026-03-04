package com.sau.pojo.DTO;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;



@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentQueryDTO {
    // 页码 默认为 1
    private Integer page = 1;
    // 每页展示记录数 默认为 10
    private Integer pageSize = 10;
    // 文章ID
    private Integer articleId;
}
