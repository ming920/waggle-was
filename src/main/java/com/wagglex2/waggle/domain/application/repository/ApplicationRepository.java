package com.wagglex2.waggle.domain.application.repository;

import com.wagglex2.waggle.domain.application.entity.Application;
import com.wagglex2.waggle.domain.common.type.RecruitmentCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    boolean existsByApplicantIdAndRecruitmentIdAndIsDeletedFalse(Long applicantId, Long recruitmentId);

    /**
     * 조회 시 Application과 연관된 Recruitment 및 Recruitment의 User를 즉시 로딩
     */
    @Query("""
        SELECT a
        FROM Application a
        JOIN FETCH a.recruitment r
        JOIN FETCH r.user u
        WHERE a.id = :id
        AND a.isDeleted = false
    """)
    Optional<Application> findByIdAndNotDeletedWithRecruitmentAndAuthor(Long id);

    /**
     * 특정 사용자의 지원 내역 중, 삭제 처리 되지 않은 내역을 카테고리별로 페이지 단위로 조회한다.
     */
    @EntityGraph(
            attributePaths = {"recruitment", "skills"},
            type = EntityGraph.EntityGraphType.LOAD
    )
    Page<Application> findAllByApplicantIdAndRecruitmentCategoryAndIsDeletedFalse(
            Long applicantId,
            RecruitmentCategory category,
            Pageable pageable
    );

    /**
     * 주어진 모집 공고 ID 목록에 해당하는 지원 정보를 조회한다.
     * <p>
     * 삭제되지 않고, {@code 대기중} 상태인 지원 정보를 조회하며, `createdAt` 기준 내림차순으로 정렬된다.
     * </p>
     *
     * @param recruitmentIds 조회할 모집 공고 ID 목록
     * @return 조건에 맞는 지원서 목록을 반환한다
     */
    @Query("""
        SELECT a
        FROM Application a
        JOIN FETCH a.recruitment r
        JOIN FETCH r.user
        LEFT JOIN FETCH a.skills
        WHERE r.id in :recruitmentIds
        AND a.status = com.wagglex2.waggle.domain.application.type.ApplicationStatus.SUBMITTED
        AND a.isDeleted = false
        ORDER BY a.createdAt DESC
    """)
    List<Application> findAllByRecruitmentIds(@Param("recruitmentIds") List<Long> recruitmentIds);

    /**
     * 마감된 공고에 대한 모든 지원 상태를 CLOSED로 변경한다.
     *
     * @return 업데이트된 지원 건수
     */
    @Modifying(clearAutomatically = true)
    @Query("""
        UPDATE Application a
        SET a.status = com.wagglex2.waggle.domain.application.type.ApplicationStatus.CLOSED
        WHERE a.status = com.wagglex2.waggle.domain.application.type.ApplicationStatus.SUBMITTED
        AND a.recruitment.status = com.wagglex2.waggle.domain.common.type.RecruitmentStatus.CLOSED
    """)
    int closeApplicationsForClosedRecruitments();
}
