package com.wagglex2.waggle.domain.team.repository;

import com.wagglex2.waggle.domain.common.type.RecruitmentCategory;
import com.wagglex2.waggle.domain.common.type.RecruitmentStatus;
import com.wagglex2.waggle.domain.team.entity.Team;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long>, TeamRepositoryCustom {

    Optional<Team> findByRecruitmentId(Long recruitmentId);

    @EntityGraph(
            attributePaths = {
                    "members",
                    "members.user"
            },
            type = EntityGraph.EntityGraphType.LOAD
    )
    @Query("SELECT t FROM Team t WHERE t.id = :id")
    Optional<Team> findByIdWithMembers(@Param("id") Long id);
}
