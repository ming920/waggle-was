package com.wagglex2.waggle.domain.project.entity;

import com.wagglex2.waggle.common.error.ErrorCode;
import com.wagglex2.waggle.common.exception.BusinessException;
import com.wagglex2.waggle.domain.common.dto.request.GradeRequestDto;
import com.wagglex2.waggle.domain.common.dto.request.PeriodRequestDto;
import com.wagglex2.waggle.domain.common.dto.request.PositionInfoUpdateRequestDto;
import com.wagglex2.waggle.domain.common.entity.BaseRecruitment;
import com.wagglex2.waggle.domain.common.type.*;
import com.wagglex2.waggle.domain.project.dto.request.ProjectUpdateRequestDto;
import com.wagglex2.waggle.domain.project.type.MeetingType;
import com.wagglex2.waggle.domain.project.type.ProjectPurpose;
import com.wagglex2.waggle.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 프로젝트 모집 공고 엔티티.
 * <p>
 * BaseRecruitment를 상속하여 유저, 제목, 본문, 마감일을 포함하며
 * 프로젝트 공고에 필요한 추가 필드를 정의한다.
 * </p>
 *
 * <ul>
 *   <li>{@link ProjectPurpose} : 프로젝트 목적</li>
 *   <li>{@link MeetingType} : 모임 방식 (온/오프라인)</li>
 *   <li>{@link PositionParticipantInfo} : 모집 포지션 리스트 (ElementCollection)</li>
 *   <li>{@link Skill} : 요구 기술 스택 (ElementCollection)</li>
 *   <li>grades : 지원 가능 학년 (ElementCollection)</li>
 *   <li>{@link Period} : 프로젝트 기간</li>
 * </ul>
 *
 * @author 오재민
 * @see BaseRecruitment
 */
@Table(name = "projects")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Project extends BaseRecruitment {
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProjectPurpose purpose;

    @Column(name = "meeting_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private MeetingType meetingType;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "recruitment_positions",
            joinColumns = @JoinColumn(name = "recruitment_id", referencedColumnName = "id")
    )
    private Set<PositionParticipantInfo> positions = new HashSet<>();

    @Enumerated(value = EnumType.STRING)
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "recruitment_skills",
            joinColumns = @JoinColumn(name = "recruitment_id", referencedColumnName = "id")
    )
    private Set<Skill> skills = new HashSet<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "recruitment_grades",
            joinColumns = @JoinColumn(name = "recruitment_id", referencedColumnName = "id")
    )
    private Set<Integer> grades = new HashSet<>();

    @Embedded
    private Period period;

    @Builder
    public Project(
            User user, String title, String content, LocalDateTime deadline,
            ProjectPurpose purpose, MeetingType meetingType,
            Set<PositionParticipantInfo> positions, Set<Skill> skills, Set<Integer> grades,
            Period period
    ) {
        super(user, RecruitmentCategory.PROJECT, title, content, deadline);
        this.purpose = purpose;
        this.meetingType = meetingType;
        this.positions = positions;
        this.skills = skills;
        this.grades = grades;
        this.period = period;
    }

    public void update(ProjectUpdateRequestDto dto) {
        update(dto.getTitle(), dto.getContent(), dto.getDeadline());

        Set<PositionParticipantInfo> positions = dto.getPositions().stream()
                .map(PositionInfoUpdateRequestDto::to)
                .collect(Collectors.toSet());

        Set<Integer> grades = dto.getGrades().stream()
                .map(GradeRequestDto::grade)
                .collect(Collectors.toSet());

        this.purpose = dto.getPurpose();
        this.meetingType = dto.getMeetingType();
        this.skills.clear();
        this.skills.addAll(dto.getSkills());
        this.positions.clear();
        this.positions.addAll(positions);
        this.grades.clear();
        this.grades.addAll(grades);
        this.period = PeriodRequestDto.to(dto.getPeriod());
        changeStatusByDeadline();
    }

    public Optional<PositionParticipantInfo> getPositionInfoByRole(PositionType position) {
        return positions.stream()
                .filter(p -> p.getPosition() == position)
                .findAny();
    }

    /**
     * 특정 포지션(PositionType)의 현재 참여 인원을 1명 감소시킨다.
     * <p>
     * - 팀 멤버가 탈퇴하거나 강제 제거될 때, 해당 멤버의 포지션을 기준으로 인원 수를 감소시킨다.<br>
     * - 지정된 포지션에 해당하는 {@link PositionParticipantInfo}가 존재하지 않으면 예외를 발생시킨다.<br>
     * - 내부적으로 {@link ParticipantInfo#decreaseCurrParticipants()}를 호출하여 실제 카운트를 변경한다.<br>
     * - 인원 수가 0명 이하로 내려가지 않도록 {@link ParticipantInfo}에서 검증 로직을 수행한다.<br>
     * - 동시 수정 가능성이 있으므로, 상위 서비스 계층에서는 비관적 락(PESSIMISTIC_WRITE)으로 수행한다.
     * </p>
     */
    @Override
    public void decreaseCurrParticipant(PositionType positionType) {
        PositionParticipantInfo info = positions.stream()
                .filter(p -> p.getPosition() == positionType)
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.POSITION_NOT_FOUND));

        info.getParticipantInfo().decreaseCurrParticipants();
    }
}
