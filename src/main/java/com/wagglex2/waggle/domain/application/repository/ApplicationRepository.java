package com.wagglex2.waggle.domain.application.repository;

import com.wagglex2.waggle.domain.application.entity.Application;
import com.wagglex2.waggle.domain.application.type.ApplicationStatus;
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
        JOIN FETCH a.applicant
        LEFT JOIN FETCH a.skills
        WHERE r.id in :recruitmentIds
        AND a.status = com.wagglex2.waggle.domain.application.type.ApplicationStatus.SUBMITTED
        AND a.isDeleted = false
        ORDER BY a.createdAt DESC
    """)
    List<Application> findAllByRecruitmentIds(@Param("recruitmentIds") List<Long> recruitmentIds);

    /**
     * 특정 공고에 대한 모든 지원을 취소 상태(CANCELED)로 변경한다.
     * <p>
     * 특정 공고가 삭제되는 경우, 그와 관련된 지원의 상태를 변경하는 용도이다.<br>
     * 대상은 삭제 처리 되지 않고, 대기 상태(SUBMITTED)인 지원이다.
     *
     * @param recruitmentId 공고 ID
     */
    @Modifying(clearAutomatically = true)
    @Query("""
        UPDATE Application a
        SET a.status = com.wagglex2.waggle.domain.application.type.ApplicationStatus.CANCELED
        WHERE a.recruitment.id = :recruitmentId
        AND a.isDeleted = false
        AND a.status = com.wagglex2.waggle.domain.application.type.ApplicationStatus.SUBMITTED
    """)
    void cancelAllByRecruitmentId(Long recruitmentId);

    /**
     * 특정 공고에 대한 모든 지원서의 상태를 조건에 맞춰 일괄 변경한다.
     *
     * <p>지원서 중 삭제되지 않았고 현재 상태가 {@code from}인 경우에만
     * {@code to} 상태로 업데이트된다.
     *
     * @param recruitmentId 상태를 변경할 공고 ID
     * @param from 현재 상태 조건
     * @param to 변경할 목표 상태
     */
    @Modifying(clearAutomatically = true)
    @Query("""
        UPDATE Application a
        SET a.status = :to
        WHERE a.recruitment.id = :recruitmentId
        AND a.isDeleted = false
        AND a.status = :from
    """)
    void updateStatusAllByRecruitmentId(
            @Param("recruitmentId") Long recruitmentId,
            @Param("from") ApplicationStatus from,
            @Param("to") ApplicationStatus to
    );

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
