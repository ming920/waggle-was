package com.wagglex2.waggle.domain.team_member.repository;

import com.wagglex2.waggle.domain.team_member.entity.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
}
