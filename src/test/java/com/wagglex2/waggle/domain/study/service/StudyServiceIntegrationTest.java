package com.wagglex2.waggle.domain.study.service;

import com.wagglex2.waggle.common.security.CustomUserDetails;
import com.wagglex2.waggle.domain.common.dto.request.ParticipantInfoUpdateRequestDto;
import com.wagglex2.waggle.domain.common.dto.request.PeriodRequestDto;
import com.wagglex2.waggle.domain.common.type.*;
import com.wagglex2.waggle.domain.study.dto.request.StudyUpdateRequestDto;
import com.wagglex2.waggle.domain.study.entity.Study;
import com.wagglex2.waggle.domain.study.repository.StudyRepository;
import com.wagglex2.waggle.domain.user.entity.User;
import com.wagglex2.waggle.domain.user.entity.type.University;
import com.wagglex2.waggle.domain.user.entity.type.UserRoleType;
import com.wagglex2.waggle.domain.user.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class StudyServiceIntegrationTest {

    @Autowired private StudyService studyService;
    @Autowired private StudyRepository studyRepository;
    @Autowired private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = createUser(); userRepository.save(user);
        CustomUserDetails cud = new CustomUserDetails(user);
        Authentication auth = new UsernamePasswordAuthenticationToken(cud, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @DisplayName("스터디 공고 수정 시 정상적으로 업데이트 된다 (period 포함).")
    void updateStudy() throws Exception {
        // given
        Study study = createStudy();      // 초기 상태: RECRUITING
        studyRepository.save(study);

        Long userId = study.getUser().getId();
        Long studyId = study.getId();

        LocalDateTime beforeUpdatedAt = study.getUpdatedAt();
        RecruitmentStatus beforeStatus = study.getStatus();
        assertThat(beforeStatus).isEqualTo(RecruitmentStatus.RECRUITING);

        StudyUpdateRequestDto updateDto = createUpdateDto(); // deadline 어제로 -> CLOSED 기대

        // when
        Thread.sleep(1500); // updatedAt 차이 확인용
        studyService.updateStudy(userId, studyId, updateDto);
        studyRepository.flush();

        // then
        // Project 테스트처럼 DTO와 엔티티를 재귀 비교 (타입 달라도 필드명 기준으로 비교됨)
        assertThat(study).usingRecursiveComparison()
                .ignoringFields(
                        "id", "user", "category", "createdAt", "updatedAt", "viewCount", "status"
                )
                .isEqualTo(updateDto);

        // updatedAt 갱신
        assertThat(study.getUpdatedAt()).isAfter(beforeUpdatedAt);
        // 마감일 경과 -> CLOSED
        assertThat(study.getStatus()).isEqualTo(RecruitmentStatus.CLOSED);
    }

    private StudyUpdateRequestDto createUpdateDto() {
        // period는 바뀐 값으로, deadline은 어제로 설정해 상태 전이 검증
        PeriodRequestDto period = new PeriodRequestDto(
                LocalDate.now().minusDays(7),
                LocalDate.now().plusDays(21)
        );
        LocalDateTime deadlinePast = LocalDateTime.now().minusDays(1)
                .withHour(23).withMinute(59).withSecond(59);

        return new StudyUpdateRequestDto(
                "수정된 스터디 제목",
                "수정된 스터디 본문",
                deadlinePast,
                period,
                Set.of(Skill.JAVA, Skill.SPRING_BOOT),
                new ParticipantInfoUpdateRequestDto(8, 2)
        );
    }

    private Study createStudy() {
        LocalDateTime deadlineFuture = LocalDateTime.now().plusDays(2)
                .withHour(23).withMinute(59).withSecond(59);

        return Study.builder()
                .user(user)
                .title("Spring 스터디 구합니다")
                .content("주 2회 오프라인")
                .participants(new ParticipantInfo(5))
                .period(new Period(LocalDate.now().minusDays(3), LocalDate.now().plusDays(14)))
                .skills(new HashSet<>(Set.of(Skill.JAVA)))
                .deadline(deadlineFuture)
                .build();
    }

    private User createUser() {
        return User.builder()
                .username("abc123")
                .password("pw")
                .nickname("솔랑솔랑")
                .email("abc123@waggle.com")
                .university(University.YOUNGNAM_UNIV)
                .grade(3)
                .role(UserRoleType.ROLE_USER)
                .shortIntro("Hi")
                .position(PositionType.BACK_END)
                .skills(Set.of(Skill.JAVA, Skill.SPRING_BOOT))
                .build();
    }
}
