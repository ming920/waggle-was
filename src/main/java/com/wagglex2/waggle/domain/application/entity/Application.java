package com.wagglex2.waggle.domain.application.entity;

import com.wagglex2.waggle.domain.application.type.ApplicationStatus;
import com.wagglex2.waggle.domain.common.entity.BaseRecruitment;
import com.wagglex2.waggle.domain.common.type.PositionType;
import com.wagglex2.waggle.domain.common.type.Skill;
import com.wagglex2.waggle.domain.project.type.MeetingType;
import com.wagglex2.waggle.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Table(
        name = "applications",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_applications_applicant_id_recruitment_id",
                        columnNames = {"applicant_id", "recruitment_id"}
                ),
        }
)
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", referencedColumnName = "id", nullable = false)
    private User applicant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruitment_id", referencedColumnName = "id", nullable = false)
    private BaseRecruitment recruitment;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Integer grade;

    @Enumerated(EnumType.STRING)
    @Column(name = "meeting_type", nullable = false)
    private MeetingType meetingType;

    /**
     * Project 공고에 대한 지원에만 존재<br>
     * 그 외의 경우에는 null
     */
    @Enumerated(EnumType.STRING)
    private PositionType position;

    /**
     * Project 공고에 대한 지원에만 존재<br>
     * 그 외의 경우에는 empty set
     */
    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "application_skills",
            joinColumns = @JoinColumn(name = "application_id", referencedColumnName = "id", nullable = false)
    )
    private Set<Skill> skills = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status = ApplicationStatus.SUBMITTED;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @Builder
    public Application(
            User applicant, BaseRecruitment recruitment, String content,
            Integer grade, MeetingType meetingType, PositionType position,
            Set<Skill> skills
    ) {
        this.applicant = applicant;
        this.recruitment = recruitment;
        this.content = content;
        this.grade = grade;
        this.meetingType = meetingType;
        this.position = position;
        this.skills = skills;
    }
}
