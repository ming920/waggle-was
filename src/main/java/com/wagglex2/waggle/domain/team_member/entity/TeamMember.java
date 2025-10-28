package com.wagglex2.waggle.domain.team_member.entity;

import com.wagglex2.waggle.domain.common.type.PositionType;
import com.wagglex2.waggle.domain.team.entity.Team;
import com.wagglex2.waggle.domain.team_member.entity.type.TeamRole;
import com.wagglex2.waggle.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * TeamMember 엔티티
 *
 * <p>팀(Team)에 소속된 개별 팀원 정보를 나타내는 엔티티.</p>
 *
 * <ul>
 *   <li>각 팀원은 하나의 팀에 소속된다. (N:1)</li>
 *   <li>각 팀원은 하나의 사용자(User) 정보를 참조한다. (N:1)</li>
 *   <li>팀 내 역할(리더, 일반 멤버 등)과 프로젝트 팀의 포지션(백엔드, 프론트엔드 등)을 관리한다.</li>
 * </ul>
 *
 * <p>포지션(position)은 Project 팀에서만 사용되며, Study / Assignment 팀의 경우 null 값이 허용된다.</p>
 *
 * @author 김민재
 */
@Entity
@Table(name = "team_members")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class TeamMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TeamRole role;

    /**
     * Project 팀에서만 사용되는 포지션 정보
     * Study / Assignment 팀의 경우 null 허용
     */
    @Enumerated(EnumType.STRING)
    private PositionType position;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Builder
    public TeamMember(Team team, User user, TeamRole role) {
        this.team = team;
        this.user = user;
        this.role = role;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
}
