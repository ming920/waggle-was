package com.wagglex2.waggle.domain.bookmark.service.serviceImpl;

import com.wagglex2.waggle.common.error.ErrorCode;
import com.wagglex2.waggle.common.exception.BusinessException;
import com.wagglex2.waggle.domain.bookmark.entity.Bookmark;
import com.wagglex2.waggle.domain.bookmark.repository.BookmarkRepository;
import com.wagglex2.waggle.domain.bookmark.service.BookmarkService;
import com.wagglex2.waggle.domain.common.service.RecruitmentService;
import com.wagglex2.waggle.domain.common.type.RecruitmentCategory;
import com.wagglex2.waggle.domain.project.dto.response.ProjectSummaryResponseDto;
import com.wagglex2.waggle.domain.project.service.ProjectService;
import com.wagglex2.waggle.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookmarkServiceImpl implements BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final UserService userService;
    private final RecruitmentService recruitmentService;
    private final ProjectService projectService;

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

    @PreAuthorize("#userId == authentication.principal.userId")
    @Override
    public Page<ProjectSummaryResponseDto> getBookmarkedProjectsByUserId(Long userId, Pageable pageable) {
        // 찜한 프로젝트 공고 id 조회
        Page<Long> targetIds =
                bookmarkRepository.findBookmarkedRecruitmentIdsByUserId(userId, RecruitmentCategory.PROJECT, pageable);

        // targetIds에 해당하는 프로젝트 공고 정보 조회
        List<ProjectSummaryResponseDto> projectSummaries =
                projectService.getProjectSummariesByIds(targetIds.getContent());

        return new PageImpl<>(projectSummaries, pageable, targetIds.getTotalElements());
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
}
