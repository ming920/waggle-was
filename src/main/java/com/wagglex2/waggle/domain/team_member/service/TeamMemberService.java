package com.wagglex2.waggle.domain.team_member.service;

import com.wagglex2.waggle.domain.application.entity.Application;
import com.wagglex2.waggle.domain.application.service.ApplicationService;
import com.wagglex2.waggle.domain.common.type.PositionType;
import com.wagglex2.waggle.domain.team.entity.Team;
import com.wagglex2.waggle.domain.team_member.entity.TeamMember;

public interface TeamMemberService {

    /**
     * 주어진 {@link Team} 과 {@link Application} 정보를 기반으로 새로운 {@link TeamMember} 를 생성한다.
     * <p>
     * 이 메서드는 지원서가 승인될 때 지원자를 팀원으로 추가하기 위해
     * <b>{@link ApplicationService#acceptApplication}에서 호출되도록 설계한 도메인 규칙 메서드</b>이다.
     * <p>
     * 팀원 생성 규칙은 다음과 같다.
     * <ul>
     *     <li>모집 카테고리가 {@code PROJECT} 인 경우, 지원서에 포함된 포지션 정보를 사용하여 팀원을 생성한다.</li>
     *     <li>{@code ASSIGNMENT}, {@code STUDY}의 경우, 포지션 없이 일반 팀원으로 생성한다.</li>
     * </ul>
     *
     * @param team 팀원으로 추가될 팀을 의미한다.
     * @param application 승인된 지원서를 의미한다.
     * @return 생성된 TeamMember 객체를 반환한다.
     */
    TeamMember createMember(Team team, Application application);

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
    PositionType getPositionInProject(Long projectId, Long userId);

    /**
     * 팀 멤버 삭제 (리더 권한 전용)
     *
     * <p>해당 메서드는 특정 팀의 리더가 팀 멤버를 강제 탈퇴(삭제)시키는 로직을 수행한다.</p>
     * <p>공고 엔티티(@Version 기반) 현재 인원 감소를 수행하며, 동시성 충돌 시 OptimisticLockException이 발생할 수 있다.</p>
     *
     * <ul>
     *   <li>리더만 멤버 삭제 가능</li>
     *   <li>리더 본인은 자신을 삭제할 수 없음</li>
     *   <li>존재하지 않는 팀이나 멤버에 대한 삭제 시 예외 발생</li>
     *   <li>모든 검증을 통과한 경우 실제 데이터 삭제 수행</li>
     * </ul>
     */
    void removeMember(Long teamId, Long removerId ,Long targetId);

    boolean existsByTeamIdAndUserId(Long teamId, Long userId);
}
