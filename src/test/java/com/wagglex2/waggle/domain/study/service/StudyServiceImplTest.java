package com.wagglex2.waggle.domain.study.service;

import com.wagglex2.waggle.domain.bookmark.service.BookmarkService;
import com.wagglex2.waggle.domain.common.type.ParticipantInfo;
import com.wagglex2.waggle.domain.common.type.Period;
import com.wagglex2.waggle.domain.common.type.Skill;
import com.wagglex2.waggle.domain.study.dto.response.StudyDetailResponseDto;
import com.wagglex2.waggle.domain.study.entity.Study;
import com.wagglex2.waggle.domain.study.repository.StudyRepository;
import com.wagglex2.waggle.domain.study.service.serviceImpl.StudyServiceImpl;
import com.wagglex2.waggle.domain.user.entity.User;
import com.wagglex2.waggle.domain.user.entity.type.University;
import com.wagglex2.waggle.domain.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudyServiceImplTest {

    @Mock
    private StudyRepository studyRepository;

    @Mock
    private UserService userService;

    @Mock
    private BookmarkService bookmarkService;

    @InjectMocks
    private StudyServiceImpl studyService;

    @Test
    @DisplayName("저장된 스터디 공고 상세 조회 성공")
    void getStudy() {
        // given
        Study study = createStudy();
        given(studyRepository.findWithAllById(1L)).willReturn(Optional.of(study));
        User viewer = mock(User.class);
        given(viewer.getUniversity()).willReturn(University.YOUNGNAM_UNIV);
        given(userService.findById(2L)).willReturn(viewer);
        given(bookmarkService.findIdByUserIdAndRecruitmentId(2L, 1L)).willReturn(Optional.empty());
        given(studyRepository.increaseViewCount(1L)).willReturn(1);

        // when
        StudyDetailResponseDto actual = studyService.getStudy(2L, 1L);

        // then
        StudyDetailResponseDto expected = StudyDetailResponseDto.fromEntity(study, false, null);
        assertThat(actual).usingRecursiveComparison()
                .ignoringFields("viewCount")
                .isEqualTo(expected);

        verify(studyRepository, times(1)).findWithAllById(1L);
        verify(studyRepository, times(1)).increaseViewCount(1L);
    }

    private Study createStudy() {
        User author = mock(User.class);
        given(author.getId()).willReturn(3L);
        given(author.getNickname()).willReturn("박대형");
        given(author.getUniversity()).willReturn(University.YOUNGNAM_UNIV);

        return Study.builder()
                .user(author)
                .title("Spring Boot 스터디 모집합니다.")
                .content("Spring Boot 백엔드 스터디를 진행합니다.")
                .deadline(LocalDateTime.now().plusDays(5))
                .participants(new ParticipantInfo(5))
                .period(mock(Period.class))
                .skills(Set.of(Skill.JAVA, Skill.KOTLIN))
                .build();
    }
}