package com.wagglex2.waggle.domain.team.service.serviceImpl;

import com.wagglex2.waggle.common.error.ErrorCode;
import com.wagglex2.waggle.common.exception.BusinessException;
import com.wagglex2.waggle.common.validator.PageableValidator;
import com.wagglex2.waggle.domain.common.entity.BaseRecruitment;
import com.wagglex2.waggle.domain.common.service.RecruitmentService;
import com.wagglex2.waggle.domain.common.type.PositionType;
import com.wagglex2.waggle.domain.common.type.RecruitmentCategory;
import com.wagglex2.waggle.domain.common.type.RecruitmentStatus;
import com.wagglex2.waggle.domain.team.dto.response.TeamResponseDto;
import com.wagglex2.waggle.domain.team.entity.Team;
import com.wagglex2.waggle.domain.team.repository.TeamRepository;
import com.wagglex2.waggle.domain.team.service.TeamService;
import com.wagglex2.waggle.domain.team_member.entity.TeamMember;
import com.wagglex2.waggle.domain.team_member.entity.type.TeamRole;
import com.wagglex2.waggle.domain.user.entity.User;
import com.wagglex2.waggle.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final UserService userService;
    private final PageableValidator pageableValidator;
    private final RecruitmentService recruitmentService;

    private static final Set<String> MY_TEAM_SORT_FIELDS =
            Set.of("createdAt");

    @Override
    @Transactional
    public void save(Team team) {
        teamRepository.save(team);
    }

    @Override
    public Team findById(Long id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.TEAM_NOT_FOUND));
    }

    @Override
    public Team findByIdWithMembers(Long id) {
        return teamRepository.findByIdWithMembers(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.TEAM_NOT_FOUND));
    }

    @Override
    public Team findByRecruitmentId(Long recruitmentId) {
        return teamRepository.findByRecruitmentId(recruitmentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TEAM_NOT_FOUND));
    }

    @Override
    public boolean existsById(Long id) {
        return teamRepository.existsById(id);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY) // 반드시 트랜잭션 내에서 실행
    public void createByRecruitmentId(Long userId, Long recruitmentId) {
        User user = userService.findById(userId);
        BaseRecruitment recruitment = recruitmentService.findById(recruitmentId);

        Team team = new Team(recruitment);
        TeamMember leader = new TeamMember(team, user, TeamRole.LEADER);
        team.addMember(leader);
        teamRepository.save(team);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY) // 반드시 트랜잭션 내에서 실행
    public void createByRecruitmentId(Long userId, Long recruitmentId, PositionType authorPosition) {
        User user = userService.findById(userId);
        BaseRecruitment recruitment = recruitmentService.findById(recruitmentId);

        Team team = new Team(recruitment);
        TeamMember leader = new TeamMember(team, user, TeamRole.LEADER, authorPosition);
        team.addMember(leader);
        teamRepository.save(team);
    }

    /**
     * 특정 사용자가 생성한 모집공고(프로젝트/스터디 등)에 속한 팀 목록을
     * 카테고리(category)와 상태(status)에 따라 페이징 조회하는 서비스 메서드.
     *
     * @param userId   모집공고 작성자(User)의 ID
     * @param category 모집 카테고리 (PROJECT, STUDY, ASSIGNMENT 등)
     * @param status   모집 상태 (RECRUITING, CLOSED, CANCELED 등)
     * @param pageable 페이지 및 정렬 정보
     * @return         Page 형태의 TeamResponseDto 목록
     */
    @Override
    public Page<TeamResponseDto> getByUserIdAndCategoryAndStatus(
            Long userId,
            RecruitmentCategory category,
            RecruitmentStatus status,
            Pageable pageable
    ) {

        if (userId == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        if (status == RecruitmentStatus.CANCELED) {
            throw new BusinessException(ErrorCode.INVALID_ARGUMENT);
        }

        pageableValidator.validate(pageable);
        pageableValidator.validateSort(pageable, MY_TEAM_SORT_FIELDS);

        Page<Team> teams = teamRepository.getTeams(userId, category, status, pageable);

        if (!teams.isEmpty()) {
            List<Long> teamIds = teams.getContent().stream()
                    .map(Team::getId)
                    .toList();

            // 별도 쿼리로 members 조회
            teamRepository.fetchMembers(teamIds);
        }

        return teams.map(TeamResponseDto::fromEntity);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY) // 반드시 트랜잭션 내에서 실행
    public void deleteByRecruitmentId(Long recruitmentId) {
        Team team = findByRecruitmentId(recruitmentId);

        teamRepository.delete(team);
    }
}
