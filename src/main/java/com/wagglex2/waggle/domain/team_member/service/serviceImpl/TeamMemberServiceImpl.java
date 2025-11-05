package com.wagglex2.waggle.domain.team_member.service.serviceImpl;

import com.wagglex2.waggle.common.error.ErrorCode;
import com.wagglex2.waggle.common.exception.BusinessException;
import com.wagglex2.waggle.domain.assignment.entity.Assignment;
import com.wagglex2.waggle.domain.common.entity.BaseRecruitment;
import com.wagglex2.waggle.domain.common.service.RecruitmentService;
import com.wagglex2.waggle.domain.project.entity.Project;
import com.wagglex2.waggle.domain.study.entity.Study;
import com.wagglex2.waggle.domain.team.entity.Team;
import com.wagglex2.waggle.domain.team.service.TeamService;
import com.wagglex2.waggle.domain.team_member.entity.TeamMember;
import com.wagglex2.waggle.domain.team_member.entity.type.TeamRole;
import com.wagglex2.waggle.domain.team_member.repository.TeamMemberRepository;
import com.wagglex2.waggle.domain.team_member.service.TeamMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamMemberServiceImpl implements TeamMemberService {

    private final TeamMemberRepository teamMemberRepository;
    private final TeamService teamService;
    private final RecruitmentService recruitmentService;

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
    @Override
    @Transactional
    @PreAuthorize("#removerId == authentication.principal.userId")
    @Retryable(
            retryFor = ObjectOptimisticLockingFailureException.class,
            noRetryFor = BusinessException.class,
            maxAttempts = 3
    )
    public void removeMember(Long teamId, Long removerId, Long targetId) {

        Team team = teamService.findByIdWithMembers(teamId);

        if (removerId.equals(targetId)) {
            throw new BusinessException(ErrorCode.CANNOT_REMOVE_SELF);
        }

        TeamMember remover = team.findMember(removerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.LEADER_NOT_FOUND));

        if (remover.getRole() != TeamRole.LEADER) {
            throw new BusinessException(ErrorCode.CANNOT_REMOVE_NOT_LEADER);
        }

        TeamMember target = team.findMember(targetId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TARGET_MEMBER_NOT_FOUND));

        BaseRecruitment recruitment = recruitmentService
                .findByIdNotCanceled(team.getRecruitment().getId());

        team.removeMember(target);
        recruitment.decreaseCurrParticipant(target.getPosition());
    }

    /**
     * 재시도 실패 시 처리
     */
    @Recover
    protected void recover(ObjectOptimisticLockingFailureException e, Long teamId, Long removerId, Long targetId) {
        throw new BusinessException(ErrorCode.TOO_MANY_REQUESTS);
    }
}
