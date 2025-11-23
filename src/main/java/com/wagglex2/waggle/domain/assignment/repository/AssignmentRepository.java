package com.wagglex2.waggle.domain.assignment.repository;

import com.wagglex2.waggle.domain.assignment.entity.Assignment;
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
public interface AssignmentRepository extends JpaRepository<Assignment, Long>, AssignmentRepositoryCustom {

    @EntityGraph(
            attributePaths = {"user", "grades"},
            type = EntityGraph.EntityGraphType.LOAD
    )
    Optional<Assignment> findWithAllById(Long assignmentId);

    /**
     * 특정 사용자가 작성한, 삭제되지 않은 과제 공고를 페이지 단위로 조회한다.
     *
     * @param userId 조회할 사용자의 ID
     * @param pageable 페이지네이션 정보
     * @return 삭제되지 않은 과제 공고의 페이지(Page) 객체
     */
    @Query("""
        Select a FROM Assignment a
        WHERE a.user.id = :userId
        AND a.status != com.wagglex2.waggle.domain.common.type.RecruitmentStatus.CANCELED
    """)
    Page<Assignment> findAllByUserId(@Param("userId") Long userId, Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query("update Assignment a set a.viewCount = a.viewCount + 1 where a.id = :assignmentId")
    int increaseViewCount(@Param("assignmentId") Long assignmentId);
}
