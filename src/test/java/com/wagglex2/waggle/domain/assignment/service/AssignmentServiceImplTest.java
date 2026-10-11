package com.wagglex2.waggle.domain.assignment.service;

import com.wagglex2.waggle.domain.assignment.dto.response.AssignmentDetailResponseDto;
import com.wagglex2.waggle.domain.assignment.entity.Assignment;
import com.wagglex2.waggle.domain.assignment.repository.AssignmentRepository;
import com.wagglex2.waggle.domain.assignment.service.serviceImpl.AssignmentServiceImpl;
import com.wagglex2.waggle.domain.bookmark.service.BookmarkService;
import com.wagglex2.waggle.domain.common.type.ParticipantInfo;
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
class AssignmentServiceImplTest {

    @Mock
    private AssignmentRepository assignmentRepository;

    @Mock
    private UserService userService;

    @Mock
    private BookmarkService bookmarkService;

    @InjectMocks
    private AssignmentServiceImpl assignmentService;

    @Test
    @DisplayName("저장된 과제 공고 상세 조회 성공")
    void getAssignment() {
        // given
        Assignment assignment = createAssignment();
        given(assignmentRepository.findWithAllById(1L)).willReturn(Optional.of(assignment));
        User viewer = mock(User.class);
        given(viewer.getUniversity()).willReturn(University.YOUNGNAM_UNIV);
        given(userService.findById(2L)).willReturn(viewer);
        given(bookmarkService.findIdByUserIdAndRecruitmentId(2L, 1L)).willReturn(Optional.empty());
        given(assignmentRepository.increaseViewCount(1L)).willReturn(1);

        // when
        AssignmentDetailResponseDto actual = assignmentService.getAssignment(2L, 1L);

        // then
        AssignmentDetailResponseDto expected = AssignmentDetailResponseDto.fromEntity(assignment, false, null);
        assertThat(actual).usingRecursiveComparison()
                .ignoringFields("viewCount")
                .isEqualTo(expected);

        verify(assignmentRepository, times(1)).findWithAllById(1L);
        verify(assignmentRepository, times(1)).increaseViewCount(1L);
    }

    private Assignment createAssignment() {
        User author = mock(User.class);
        given(author.getId()).willReturn(3L);
        given(author.getNickname()).willReturn("박대형");
        given(author.getUniversity()).willReturn(University.YOUNGNAM_UNIV);

        return Assignment.builder()
                .user(author)
                .title("네트워크 과제 같이 하실 분 구합니다.")
                .content("TCP/IP 정리 ppt 같이 만들 분 구해요.")
                .department("컴퓨터공학과")
                .lecture("컴퓨터 네트워크")
                .lectureCode("CSE302")
                .participants(new ParticipantInfo(4))
                .grades(Set.of(3, 4))
                .deadline(LocalDateTime.now().plusDays(5))
                .build();
    }
}