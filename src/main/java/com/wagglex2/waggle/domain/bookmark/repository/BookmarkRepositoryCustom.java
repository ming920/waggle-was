package com.wagglex2.waggle.domain.bookmark.repository;

import com.wagglex2.waggle.domain.common.type.RecruitmentCategory;
import com.wagglex2.waggle.domain.common.type.RecruitmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookmarkRepositoryCustom {

    /**
     * 특정 사용자가 카테고리별로 찜한 공고 ID 목록을 페이지 단위로 조회한다.
     *
     * @param userId   사용자 ID
     * @param category 공고 카테고리
     * @param status 공고 상태 (null이면 {@code 모집 중}, {@code 마감} 모두 조회)
     * @param pageable 페이징 정보
     * @return 사용자가 찜한 공고 ID를 담은 Page 객체
     */
    Page<Long> findBookmarkedRecruitmentIdsByUserId(Long userId, RecruitmentCategory category, RecruitmentStatus status, Pageable pageable);
}
