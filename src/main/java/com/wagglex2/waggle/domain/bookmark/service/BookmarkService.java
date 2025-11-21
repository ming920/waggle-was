package com.wagglex2.waggle.domain.bookmark.service;

import com.wagglex2.waggle.domain.project.dto.response.ProjectSummaryResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface BookmarkService {

    Long createBookmark(Long userId, Long recruitmentId);

    /**
     * 특정 사용자가 특정 공고를 찜했는지 여부를 조회한다.
     *
     * @param userId       사용자 ID
     * @param recruitmentId 공고 ID
     * @return 찜이 되어 있으면 해당 찜 ID를 Optional에 담아 반환하며,
     *         찜하지 않았으면 Optional.empty()를 반환한다.
     */
    Optional<Long> findIdByUserIdAndRecruitmentId(Long userId, Long recruitmentId);
    Page<ProjectSummaryResponseDto> getBookmarkedProjectsByUserId(Long userId, Pageable pageable);
    void deleteBookmark(Long userId, Long bookmarkId);
}
