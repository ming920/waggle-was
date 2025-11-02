package com.wagglex2.waggle.domain.application.repository;

import com.wagglex2.waggle.domain.application.entity.Application;
import com.wagglex2.waggle.domain.common.type.RecruitmentCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    boolean existsByApplicantIdAndRecruitmentId(Long applicantId, Long recruitmentId);

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
