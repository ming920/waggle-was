package com.wagglex2.waggle.domain.project.service.serviceImpl;

import com.wagglex2.waggle.common.error.ErrorCode;
import com.wagglex2.waggle.common.exception.BusinessException;
import com.wagglex2.waggle.domain.common.dto.response.PositionInfoResponseDto;
import com.wagglex2.waggle.domain.common.type.RecruitmentStatus;
import com.wagglex2.waggle.domain.common.type.Skill;
import com.wagglex2.waggle.domain.project.dto.request.ProjectCreationRequestDto;
import com.wagglex2.waggle.domain.project.dto.request.ProjectSearchCondition;
import com.wagglex2.waggle.domain.project.dto.request.ProjectUpdateRequestDto;
import com.wagglex2.waggle.domain.project.dto.response.ProjectDetailResponseDto;
import com.wagglex2.waggle.domain.project.dto.response.ProjectSummaryResponseDto;
import com.wagglex2.waggle.domain.project.entity.Project;
import com.wagglex2.waggle.domain.project.repository.ProjectRepository;
import com.wagglex2.waggle.domain.project.service.ProjectService;
import com.wagglex2.waggle.domain.team.entity.Team;
import com.wagglex2.waggle.domain.team.service.TeamService;
import com.wagglex2.waggle.domain.team_member.entity.TeamMember;
import com.wagglex2.waggle.domain.team_member.entity.type.TeamRole;
import com.wagglex2.waggle.domain.user.entity.User;
import com.wagglex2.waggle.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final UserService userService;
    private final TeamService teamService;

    @Transactional
    @Override
    public Long createProject(Long userId, ProjectCreationRequestDto requestDto) {
        requestDto.validate();
        User user = userService.findById(userId);
        Project newProject = ProjectCreationRequestDto.toEntity(user, requestDto);

        Long projectId = projectRepository.save(newProject).getId();

        Team team = new Team(newProject);
        TeamMember leader = new TeamMember(team, user, TeamRole.LEADER);
        team.addMember(leader);
        teamService.save(team);

        return projectId;
    }

    @Transactional
    @Override
    public ProjectDetailResponseDto getProject(Long projectId) {
        int updated = projectRepository.increaseViewCount(projectId);

        if (updated == 0) {
            throw new BusinessException(ErrorCode.PROJECT_NOT_FOUND);
        }

        // 각 collection 정보는 추가 쿼리로 조회 후 dto에 추가
        // Project 정보와 User 정보
        ProjectDetailResponseDto responseDto = projectRepository.findByIdWithUser(projectId)
                .map(ProjectDetailResponseDto::fromEntity)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROJECT_NOT_FOUND));

        // Position 정보
        Set<PositionInfoResponseDto> positions = projectRepository.findPositionsByProjectId(projectId).stream()
                .map(PositionInfoResponseDto::from)
                .collect(Collectors.toSet());

        // Skill 정보
        Set<Skill> skills = projectRepository.findSkillsByProjectId(projectId);

        // Grade 정보
        Set<Integer> grades = projectRepository.findGradesByProjectId(projectId);

        responseDto.setPositions(positions);
        responseDto.setSkills(skills);
        responseDto.setGrades(grades);

        return responseDto;
    }

    @Override
    public Page<ProjectSummaryResponseDto> getProjectSummaries(
            ProjectSearchCondition condition,
            Pageable pageable
    ) {
        return projectRepository.getProjectSummaries(condition, pageable);
    }

    /**
     * 주어진 Project ID 목록에 해당하는 프로젝트 요약 정보를 조회한다.
     * <p>
     * <ul>
     *      <li>컨트롤러 요청이 아닌 다른 서비스에서 호출하기 위한 메서드</li>
     *      <li>반환 리스트는 입력된 ID 순서를 보장</li>
     * </ul>
     *
     * @param projectIds 조회할 Project ID 목록
     * @return 입력 ID 순서에 맞춘 {@code List<ProjectSummaryResponseDto>}
     */
    @Override
    public List<ProjectSummaryResponseDto> getProjectSummariesByIds(List<Long> projectIds) {
        return projectRepository.getProjectSummariesByIds(projectIds);
    }

    @PreAuthorize("#userId == authentication.principal.userId")
    @Transactional
    @Override
    public void updateProject(@P("userId") Long userId, Long projectId, ProjectUpdateRequestDto updateDto) {
        updateDto.validate();
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROJECT_NOT_FOUND));

        // 권한 검증
        if (!userId.equals(project.getUser().getId())) {
            throw new BusinessException(ErrorCode.CANNOT_UPDATE_ANOTHER_USER_PROJECT);
        }

        // 삭제 여부 검증
        if (project.getStatus() == RecruitmentStatus.CANCELED) {
            return;
        }

        project.update(updateDto);
    }

    @PreAuthorize("#userId == authentication.principal.userId")
    @Transactional
    @Override
    public void deleteProject(@P("userId") Long userId, Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROJECT_NOT_FOUND));

        // 권한 검증
        if (!userId.equals(project.getUser().getId())) {
            throw new BusinessException(ErrorCode.CANNOT_DELETE_ANOTHER_USER_PROJECT);
        }

        // 논리적 삭제
        project.cancel();
    }
}
