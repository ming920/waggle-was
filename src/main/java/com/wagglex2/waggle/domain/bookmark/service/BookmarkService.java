package com.wagglex2.waggle.domain.bookmark.service;

import com.wagglex2.waggle.domain.project.dto.response.ProjectSummaryResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookmarkService {

    Long createBookmark(Long userId, Long recruitmentId);
    Page<ProjectSummaryResponseDto> getBookmarkedProjectsByUserId(Long userId, Pageable pageable);
    void deleteBookmark(Long userId, Long bookmarkId);
}
