package com.wagglex2.waggle.domain.bookmark.service.serviceImpl;

import com.wagglex2.waggle.common.error.ErrorCode;
import com.wagglex2.waggle.common.exception.BusinessException;
import com.wagglex2.waggle.domain.bookmark.entity.Bookmark;
import com.wagglex2.waggle.domain.bookmark.repository.BookmarkRepository;
import com.wagglex2.waggle.domain.bookmark.service.BookmarkService;
import com.wagglex2.waggle.domain.common.entity.BaseRecruitment;
import com.wagglex2.waggle.domain.common.service.RecruitmentService;
import com.wagglex2.waggle.domain.common.type.RecruitmentCategory;
import com.wagglex2.waggle.domain.common.type.RecruitmentStatus;
import com.wagglex2.waggle.domain.user.entity.User;
import com.wagglex2.waggle.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
        User user = userService.findById(userId);
        BaseRecruitment recruitment = recruitmentService.findById(recruitmentId);

        // 타 대학 공고를 찜하려는 경우
        if (user.getUniversity() != recruitment.getUser().getUniversity()) {
            throw new BusinessException(ErrorCode.FORBIDDEN_CROSS_UNIVERSITY_RECRUITMENT);
        }

        // 이미 찜한 경우
        if (bookmarkRepository.existsByUserIdAndRecruitmentId(userId, recruitmentId)) {
            throw new BusinessException(ErrorCode.ALREADY_BOOKMARKED);
        }

        Bookmark newBookmark = new Bookmark(user, recruitment);

        return bookmarkRepository.save(newBookmark).getId();
    }

    @Override
    public Page<Long> findBookmarkedRecruitmentIdsByUserId(Long userId, RecruitmentCategory category, RecruitmentStatus status, Pageable pageable) {
        if (status == RecruitmentStatus.CANCELED) {
            throw new BusinessException(ErrorCode.INVALID_ARGUMENT);
        }

        return bookmarkRepository.findBookmarkedRecruitmentIdsByUserId(userId, category, status, pageable);
    }

    @Override
    public Optional<Long> findIdByUserIdAndRecruitmentId(Long userId, Long recruitmentId) {
        return bookmarkRepository.findIdByUserIdAndRecruitmentId(userId, recruitmentId);
    }

    @PreAuthorize("#userId == authentication.principal.userId")
    @Transactional
    @Override
    public void deleteBookmark(@P("userId") Long userId, Long bookmarkId) {
        Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOOKMARK_NOT_FOUND));

        // 권한 검증
        if (!userId.equals(bookmark.getUser().getId())) {
            throw new BusinessException(ErrorCode.CANNOT_DELETE_ANOTHER_USER_BOOKMARK);
        }

        bookmarkRepository.delete(bookmark);
    }

    @Transactional
    @Override
    public void deleteAllByRecruitmentId(Long recruitmentId) {
        bookmarkRepository.deleteAllByRecruitmentId(recruitmentId);
    }
}
