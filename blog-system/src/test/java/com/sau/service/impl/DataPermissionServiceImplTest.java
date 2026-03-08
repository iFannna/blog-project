package com.sau.service.impl;

import com.sau.mapper.ArticleMapper;
import com.sau.mapper.CommentMapper;
import com.sau.pojo.entity.Article;
import com.sau.security.AuthUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DataPermissionServiceImplTest {

    @Mock
    private ArticleMapper articleMapper;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private DataPermissionServiceImpl dataPermissionService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void assertAdminOrArticleOwner_shouldRejectForeignArticle() {
        setCurrentUser(AuthUser.of(1, "user", List.of("ROLE_USER"), List.of("article:delete")));
        Article selfArticle = new Article();
        selfArticle.setId(1);
        selfArticle.setAuthorId(1);
        Article foreignArticle = new Article();
        foreignArticle.setId(2);
        foreignArticle.setAuthorId(2);
        when(articleMapper.selectOwnershipByIds(List.of(1, 2))).thenReturn(List.of(selfArticle, foreignArticle));

        assertThrows(AccessDeniedException.class,
                () -> dataPermissionService.assertAdminOrArticleOwner(List.of(1, 2)));
    }

    @Test
    void assertAdminOrCommentOwner_shouldBypassForAdmin() {
        setCurrentUser(AuthUser.of(1, "admin", List.of("ROLE_ADMIN"), List.of("comment:delete")));

        assertDoesNotThrow(() -> dataPermissionService.assertAdminOrCommentOwner(99));
        verifyNoInteractions(commentMapper);
    }

    private void setCurrentUser(AuthUser authUser) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(authUser, null, authUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}