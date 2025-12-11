package com.wagglex2.waggle.domain.team_member.repository;

import com.wagglex2.waggle.domain.common.type.PositionType;
import com.wagglex2.waggle.domain.team_member.entity.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    Optional<TeamMember> findByTeamIdAndUserId(Long teamId, Long userId);

    /**
     * 특정 프로젝트 공고에서 사용자가 속한 포지션을 조회합니다.
     *
     * <p>이 메서드는 주어진 프로젝트 공고 ID와 사용자 ID를 기반으로,
     * 사용자가 해당 공고에서 어떤 포지션에 속해 있는지 반환한다.
     *
     * <p>
     * 주로 프로젝트 공고 상세 조회에서 리더의 포지션을 조회하기 위해 사용된다.
     *
     * @param projectId 프로젝트 공고 ID
     * @param userId 포지션 정보를 조회할 사용자 ID
     * @return 사용자가 해당 공고에서 속한 포지션
     */
    @Query("""
        SELECT m.position FROM TeamMember m
        WHERE m.team.recruitment.id = :projectId
        AND m.user.id = :userId
    """)
    PositionType getPositionInProject(Long projectId, Long userId);

    boolean existsByTeamIdAndUserId(Long teamId, Long userId);
}
