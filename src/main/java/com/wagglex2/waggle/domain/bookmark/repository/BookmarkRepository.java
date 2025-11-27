package com.wagglex2.waggle.domain.bookmark.repository;

import com.wagglex2.waggle.domain.bookmark.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long>, BookmarkRepositoryCustom {

    boolean existsByUserIdAndRecruitmentId(Long userId, Long recruitmentId);

    /**
     * 특정 사용자가 특정 공고를 찜했는지 여부를 조회한다.
     *
     * @param userId       사용자 ID
     * @param recruitmentId 공고 ID
     * @return 찜이 되어 있으면 해당 찜 ID를 Optional에 담아 반환하며,
     *         찜하지 않았으면 Optional.empty()를 반환한다.
     */
    @Query("""
        SELECT b.id
        FROM Bookmark b
        WHERE b.user.id = :userId
        AND b.recruitment.id = :recruitmentId
    """)
    Optional<Long> findIdByUserIdAndRecruitmentId(Long userId, Long recruitmentId);

    /**
     * 특정 공고에 대한 모든 찜을 삭제한다.
     *
     * @param recruitmentId 공고 ID
     */
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Bookmark b WHERE b.recruitment.id = :recruitmentId")
    int deleteAllByRecruitmentId(Long recruitmentId);
}
