package com.sau.mapper;

import com.sau.pojo.DTO.GuestBookQueryDTO;
import com.sau.pojo.entity.GuestBook;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface GuestBookMapper {
    List<GuestBook> selectPageGuestBooks(GuestBookQueryDTO guestBookQueryDTO);

    List<GuestBook> selectFeaturedGuestBooks(@Param("limit") Integer limit);

    void insertGuestBook(GuestBook guestBook);

    @Select("select id, user_id, status from guest_book where id = #{id}")
    GuestBook selectOwnershipById(Integer id);

    @Update("update guest_book set status = 0 where id = #{id} and status = 1")
    int reportById(Integer id);

    @Delete("delete from guest_book where id = #{id}")
    int deleteById(Integer id);
}
