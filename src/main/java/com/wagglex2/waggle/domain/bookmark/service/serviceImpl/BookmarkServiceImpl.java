package com.wagglex2.waggle.domain.bookmark.service.serviceImpl;

import com.wagglex2.waggle.common.error.ErrorCode;
import com.wagglex2.waggle.common.exception.BusinessException;
import com.wagglex2.waggle.domain.bookmark.entity.Bookmark;
import com.wagglex2.waggle.domain.bookmark.repository.BookmarkRepository;
import com.wagglex2.waggle.domain.bookmark.service.BookmarkService;
import com.wagglex2.waggle.domain.common.service.RecruitmentService;
import com.wagglex2.waggle.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookmarkServiceImpl implements BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final UserService userService;
    private final RecruitmentService recruitmentService;

    @PreAuthorize("#userId == authentication.principal.userId")
    @Transactional
    @Override
    public Long createBookmark(@P("userId") Long userId, Long recruitmentId) {
        // 이미 찜한 경우
        if (bookmarkRepository.existsByUserIdAndRecruitmentId(userId, recruitmentId)) {
            throw new BusinessException(ErrorCode.ALREADY_BOOKMARKED);
        }

        Bookmark newBookmark = new Bookmark(
                userService.findById(userId),
                recruitmentService.findById(recruitmentId)
        );

        return bookmarkRepository.save(newBookmark).getId();
    }
}
