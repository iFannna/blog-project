package com.sau.pojo.entity;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleQueryParam {
    // 页码
    private Integer page = 1;
    // 每页展示记录数 默认为 10
    private Integer pageSize = 10;
    // 文章类型
    private Integer type;
    // 分类ID
    private List<Integer> categoryId;
    // 标签ID
    private List<Integer> tagId;
    // 作者
    private String authorName;
    // 标题
    private String title;
    // 文章发布时间段-开始
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate begin;
    // 文章发布时间段-结束
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate end;

    // 前端无需传入-后端自动计算
    // 分类ID集合大小
    private Integer categoryIdSize;
    // 标签ID集合大小
    private Integer tagIdSize;

    public void setCategoryId(List<Integer> categoryId) {
        this.categoryId = categoryId;
        this.categoryIdSize = (categoryId == null) ? 0 : categoryId.size();
    }

    public void setTagId(List<Integer> tagId) {
        this.tagId = tagId;
        this.tagIdSize = (tagId == null) ? 0 : tagId.size();
    }
}
