package com.wagglex2.waggle.domain.study.repository;

import com.wagglex2.waggle.domain.study.entity.Study;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudyRepository extends JpaRepository<Study, Long>, StudyRepositoryCustom {

    @EntityGraph(
            attributePaths = {"user", "skills"},
            type = EntityGraph.EntityGraphType.LOAD
    )
    Optional<Study> findWithAllById(Long studyId);

    /**
     * 특정 사용자가 작성한, 삭제되지 않은 스터디 공고를 페이지 단위로 조회한다.
     *
     * @param userId 조회할 사용자의 ID
     * @param pageable 페이지네이션 정보
     * @return 삭제되지 않은 스터디 공고의 페이지(Page) 객체
     */
    @Query("""
        Select s FROM Study s
        WHERE s.user.id = :userId
        AND s.status != com.wagglex2.waggle.domain.common.type.RecruitmentStatus.CANCELED
    """)
    Page<Study> findAllByUserId(@Param("userId") Long userId, Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query("update Study a set a.viewCount = a.viewCount + 1 where a.id = :studyId")
    int increaseViewCount(@Param("studyId") Long studyId);
}