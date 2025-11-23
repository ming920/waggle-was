package com.wagglex2.waggle.domain.project.repository;

import com.wagglex2.waggle.domain.project.entity.Project;
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
public interface ProjectRepository extends JpaRepository<Project, Long>, ProjectRepositoryCustom {

    @EntityGraph(
            attributePaths = {"user", "positions", "skills", "grades"},
            type = EntityGraph.EntityGraphType.LOAD
    )
    Optional<Project> findWithAllById(Long id);

    /**
     * 특정 사용자가 작성한, 삭제되지 않은 프로젝트 공고를 페이지 단위로 조회한다.
     *
     * @param userId 조회할 사용자의 ID
     * @param pageable 페이지네이션 정보
     * @return 삭제되지 않은 프로젝트 공고의 페이지(Page) 객체
     */
    @Query("""
        Select p FROM Project p
        WHERE p.user.id = :userId
        AND p.status != com.wagglex2.waggle.domain.common.type.RecruitmentStatus.CANCELED
    """)
    Page<Project> findAllByUserId(@Param("userId") Long userId,Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Project p SET p.viewCount = p.viewCount + 1 WHERE p.id = :id")
    int increaseViewCount(@Param("id") Long id);
}
