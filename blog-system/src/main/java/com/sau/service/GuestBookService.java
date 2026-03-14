package com.sau.service;

import com.sau.pojo.DTO.GuestBookCreateDTO;
import com.sau.pojo.DTO.GuestBookQueryDTO;
import com.sau.pojo.entity.GuestBook;
import com.sau.pojo.entity.PageResult;

import java.util.List;

public interface GuestBookService {
    PageResult<GuestBook> pageQueryGuestBooks(GuestBookQueryDTO guestBookQueryDTO);

    List<GuestBook> queryFeaturedGuestBooks(Integer limit);

    void createGuestBook(GuestBookCreateDTO guestBookCreateDTO);

    void reportGuestBook(Integer id);

    void deleteGuestBook(Integer id);
}
