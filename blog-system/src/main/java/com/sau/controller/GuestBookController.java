package com.sau.controller;

import com.sau.pojo.DTO.GuestBookCreateDTO;
import com.sau.pojo.DTO.GuestBookQueryDTO;
import com.sau.pojo.entity.GuestBook;
import com.sau.pojo.entity.PageResult;
import com.sau.pojo.entity.Result;
import com.sau.service.GuestBookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/guestbook")
public class GuestBookController {
    private final GuestBookService guestBookService;

    @GetMapping
    public Result<PageResult<GuestBook>> pageQueryGuestBooks(GuestBookQueryDTO guestBookQueryDTO) {
        log.info("分页查询留言, 参数:{}", guestBookQueryDTO);
        return Result.success(guestBookService.pageQueryGuestBooks(guestBookQueryDTO));
    }

    @GetMapping("/featured")
    public Result<List<GuestBook>> queryFeaturedGuestBooks(
            @RequestParam(required = false, defaultValue = "16") Integer limit) {
        return Result.success(guestBookService.queryFeaturedGuestBooks(limit));
    }

    @PostMapping
    public Result createGuestBook(@Valid @RequestBody GuestBookCreateDTO guestBookCreateDTO) {
        guestBookService.createGuestBook(guestBookCreateDTO);
        return Result.success();
    }

    @PostMapping("/{id}/report")
    public Result reportGuestBook(@PathVariable Integer id) {
        guestBookService.reportGuestBook(id);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result deleteGuestBook(@PathVariable Integer id) {
        guestBookService.deleteGuestBook(id);
        return Result.success();
    }
}
