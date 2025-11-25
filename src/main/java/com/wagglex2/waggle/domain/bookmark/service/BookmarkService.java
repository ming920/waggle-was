package com.wagglex2.waggle.domain.bookmark.service;

import com.wagglex2.waggle.domain.bookmark.event.BookmarkEventListener;
import com.wagglex2.waggle.domain.common.type.RecruitmentCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface BookmarkService {

    Long createBookmark(Long userId, Long recruitmentId);

    /**
     * 특정 사용자가 카테고리별로 찜한 공고 ID 목록을 페이지 단위로 조회한다.
     *
     * @param userId   사용자 ID
     * @param category 공고 카테고리
     * @param pageable 페이징 정보
     * @return 사용자가 찜한 공고 ID를 담은 Page 객체
     */
    Page<Long> findBookmarkedRecruitmentIdsByUserId(Long userId, RecruitmentCategory category, Pageable pageable);

    /**
     * 특정 사용자가 특정 공고를 찜했는지 여부를 조회한다.
     *
     * @param userId       사용자 ID
     * @param recruitmentId 공고 ID
     * @return 찜이 되어 있으면 해당 찜 ID를 Optional에 담아 반환하며,
     *         찜하지 않았으면 Optional.empty()를 반환한다.
     */
    Optional<Long> findIdByUserIdAndRecruitmentId(Long userId, Long recruitmentId);
    void deleteBookmark(Long userId, Long bookmarkId);

    /**
     * 특정 공고가 삭제될 경우, 해당 공고에 대한 모든 찜을 삭제한다.
     * <p>
     * 이 메서드는 {@link BookmarkEventListener} 이벤트 리스너에서 호출되어,
     * 공고 삭제에 따른 찜 삭제를 처리한다.
     *
     * @param recruitmentId 삭제된 공고의 ID
     */
    void deleteAllByRecruitmentId(Long recruitmentId);
}
