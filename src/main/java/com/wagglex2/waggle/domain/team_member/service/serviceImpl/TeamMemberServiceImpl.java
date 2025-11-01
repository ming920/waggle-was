package com.wagglex2.waggle.domain.team_member.service.serviceImpl;

import com.wagglex2.waggle.common.error.ErrorCode;
import com.wagglex2.waggle.common.exception.BusinessException;
import com.wagglex2.waggle.domain.team.service.TeamService;
import com.wagglex2.waggle.domain.team_member.entity.TeamMember;
import com.wagglex2.waggle.domain.team_member.entity.type.TeamRole;
import com.wagglex2.waggle.domain.team_member.repository.TeamMemberRepository;
import com.wagglex2.waggle.domain.team_member.service.TeamMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamMemberServiceImpl implements TeamMemberService {

    private final TeamMemberRepository teamMemberRepository;
    private final TeamService teamService;

    /**
     * 팀 멤버 삭제 (리더 권한 전용)
     *
     * <p>해당 메서드는 특정 팀의 리더가 팀 멤버를 강제 탈퇴(삭제)시키는 로직을 수행한다.</p>
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
    public void removeMember(Long teamId, Long removerId, Long targetId) {

        if (!teamService.existsById(teamId)) {
            throw new BusinessException(ErrorCode.TEAM_NOT_FOUND);
        }

        if (removerId.equals(targetId)) {
            throw new BusinessException(ErrorCode.CANNOT_REMOVE_SELF);
        }

        TeamMember remover = teamMemberRepository.findByTeamIdAndUserId(teamId, removerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.LEADER_NOT_FOUND));

        if (remover.getRole() != TeamRole.LEADER) {
            throw new BusinessException(ErrorCode.CANNOT_REMOVE_NOT_LEADER);
        }

        TeamMember target = teamMemberRepository.findByTeamIdAndUserId(teamId, targetId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TARGET_MEMBER_NOT_FOUND));

        teamMemberRepository.delete(target);
    }
}
