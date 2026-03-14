package com.sau.service;

import java.util.List;

public interface DataPermissionService {

    boolean isAdmin();

    void assertAdminOrSelfUser(Integer targetUserId);

    void assertAdminOrArticleOwner(List<Integer> articleIds);

    void assertAdminOrCommentOwner(Integer commentId);

    void assertAdminOrGuestBookOwner(Integer guestBookId);
}
