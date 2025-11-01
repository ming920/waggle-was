package com.wagglex2.waggle.domain.team_member.controller;

import com.wagglex2.waggle.common.response.ApiResponse;
import com.wagglex2.waggle.common.security.CustomUserDetails;
import com.wagglex2.waggle.domain.team_member.service.TeamMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/teams/{teamId}/members")
@RequiredArgsConstructor
public class TeamMemberController {

    private final TeamMemberService teamMemberService;


    /**
     * 팀 멤버 삭제 API
     *
     * <p>팀 리더가 특정 멤버를 팀에서 삭제할 때 사용하는 엔드포인트</p>
     *
     * @param teamId     삭제 대상 멤버가 속한 팀의 식별자
     * @param memberId   삭제할 팀 멤버의 식별자
     * @return 성공 메시지를 담은 {@link ApiResponse}
     */
    @DeleteMapping("/{memberId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> deleteMember(
            @PathVariable Long teamId,
            @PathVariable Long memberId,
            @AuthenticationPrincipal CustomUserDetails userDetails
            ) {

        teamMemberService.removeMember(teamId, userDetails.getUserId(), memberId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("팀 멤버 삭제에 성공했습니다."));
    }
}
