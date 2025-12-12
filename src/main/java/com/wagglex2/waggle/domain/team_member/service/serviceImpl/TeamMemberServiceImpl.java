package com.wagglex2.waggle.domain.team_member.service.serviceImpl;

import com.wagglex2.waggle.common.error.ErrorCode;
import com.wagglex2.waggle.common.exception.BusinessException;
import com.wagglex2.waggle.domain.application.entity.Application;
import com.wagglex2.waggle.domain.common.entity.BaseRecruitment;
import com.wagglex2.waggle.domain.common.service.RecruitmentService;
import com.wagglex2.waggle.domain.common.type.PositionType;
import com.wagglex2.waggle.domain.common.type.RecruitmentCategory;
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

    @Override
    public TeamMember createMember(Team team, Application application) {
        if (application.getRecruitment().getCategory() == RecruitmentCategory.PROJECT) {
            return new TeamMember(
                    team,
                    application.getApplicant(),
                    TeamRole.MEMBER,
                    application.getPosition()
            );
        }

        return new TeamMember(team, application.getApplicant(), TeamRole.MEMBER);
    }

    @Override
    public PositionType getPositionInProject(Long projectId, Long userId) {
        return teamMemberRepository.getPositionInProject(projectId, userId);
    }

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

    @Override
    public boolean existsByTeamIdAndUserId(Long teamId, Long userId) {
        return teamMemberRepository.existsByTeamIdAndUserId(teamId, userId);
    }

    /**
     * 재시도 실패 시 처리
     */
    @Recover
    protected void recover(ObjectOptimisticLockingFailureException e, Long teamId, Long removerId, Long targetId) {
        throw new BusinessException(ErrorCode.TOO_MANY_REQUESTS);
    }

    @Recover
    protected void recover(BusinessException e) {
        throw e;
    }
}
