package com.wagglex2.waggle.domain.bookmark.repository;

import com.wagglex2.waggle.domain.bookmark.entity.Bookmark;
import com.wagglex2.waggle.domain.common.type.RecruitmentCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    boolean existsByUserIdAndRecruitmentId(Long userId, Long recruitmentId);

    /**
     * 특정 사용자가 북마크한 Recruitment ID를 조회한다.
     */
    @Query("""
        SELECT b.recruitment.id FROM Bookmark b
        WHERE b.user.id = :userId
        AND b.recruitment.category = :category
        ORDER BY b.recruitment.createdAt DESC
    """)
    Page<Long> findBookmarkedRecruitmentIdsByUserId(Long userId, RecruitmentCategory category, Pageable pageable);
}
