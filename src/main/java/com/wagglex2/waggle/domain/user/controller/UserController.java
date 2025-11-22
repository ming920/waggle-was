package com.wagglex2.waggle.domain.user.controller;

import com.wagglex2.waggle.common.error.ErrorCode;
import com.wagglex2.waggle.common.exception.BusinessException;
import com.wagglex2.waggle.common.response.APIResponse;
import com.wagglex2.waggle.common.security.CustomUserDetails;
import com.wagglex2.waggle.domain.review.dto.response.ReviewResponseDto;
import com.wagglex2.waggle.domain.review.service.ReviewService;
import com.wagglex2.waggle.domain.user.controller.docs.UserControllerDocs;
import com.wagglex2.waggle.domain.user.dto.request.*;
import com.wagglex2.waggle.domain.user.dto.response.UserResponseDto;
import com.wagglex2.waggle.domain.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController implements UserControllerDocs {

    private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";

    private final UserService userService;
    private final ReviewService reviewService;

    /**
     * 아이디 중복 여부를 검사한다.
     * 검사할 사용자 로그인 ID (영문, 숫자, 언더스코어 4~20자)
     *
     * @return APIResponse(Boolean) — 중복이면 true, 사용 가능이면 false
     */
    @GetMapping("/username/check")
    public ResponseEntity<APIResponse<Boolean>> existsByUsername(
            @RequestBody @Valid UsernameCheckRequestDto dto
            ) {
        boolean exists = userService.existsByUsername(dto.username());

        if (exists) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(APIResponse.ok("이미 사용 중인 아이디입니다.", true));
        } else {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(APIResponse.ok("사용 가능한 아이디입니다.", false));
        }
    }

    /**
     * 이메일 중복 여부를 검사한다.
     *
     * @return APIResponse(Boolean) — 중복이면 true, 사용 가능이면 false
     */
    @GetMapping("/email/check")
    public ResponseEntity<APIResponse<Boolean>> existsByEmail(
            @RequestBody @Valid EmailCheckRequestDto dto
            ) {

        boolean exists = userService.existsByEmail(dto.email());

        if (exists) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(APIResponse.ok("이미 사용 중인 이메일입니다.", true));
        } else {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(APIResponse.ok("사용 가능한 이메일입니다.", false));
        }
    }

    /**
     * 닉네임 중복 여부를 검사한다.
     *
     * @return APIResponse(Boolean) — 중복이면 true, 사용 가능이면 false
     */
    @GetMapping("/nickname/check")
    public ResponseEntity<APIResponse<Boolean>> existsByNickname(
            @RequestBody @Valid NicknameCheckRequestDto dto
    ) {
        boolean exists = userService.existsByNickname(dto.nickname());
        if (exists) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(APIResponse.ok("이미 사용 중인 닉네임입니다.", true));
        } else {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(APIResponse.ok("사용 가능한 닉네임입니다.", false));
        }
    }

    /**
     * 비밀번호를 변경한다.
     *
     * <p>처리 순서:</p>
     * <ol>
     *   <li>요청으로부터 기존 비밀번호, 새 비밀번호, 확인 비밀번호를 전달받는다.</li>
     *   <li>인증된 사용자(@AuthenticationPrincipal)에서 userId를 추출한다.</li>
     *   <li>서비스 계층(userService.changePassword)에서 비밀번호 검증 및 변경 로직을 처리한다.</li>
     *   <li>비밀번호 변경 성공 시 성공 응답(ApiResponse<Void>)을 반환한다.</li>
     * </ol>
     *
     * @param dto         비밀번호 변경 요청 DTO (기존 비밀번호, 새 비밀번호, 확인 비밀번호 포함)
     * @param userDetails 현재 인증된 사용자 정보
     */
    @PostMapping("/me/password-change")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<APIResponse<Void>> passwordChange(
            @Valid @RequestBody PasswordRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        userService.changePassword(userDetails.getUserId(), dto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.ok("비밀번호 변경에 성공했습니다."));
    }

    /**
     * 현재 로그인한 사용자의 정보를 조회한다.
     *
     * <p>처리 흐름:</p>
     * <ol>
     *   <li>Spring Security의 {@code @AuthenticationPrincipal}을 통해 인증된 사용자 정보(CustomUserDetails) 획득</li>
     *   <li>해당 userId를 기반으로 {@code userService.getUserInfo()} 호출 → UserResponseDto 변환</li>
     *   <li>ApiResponse 래핑을 통해 일관된 응답 형식으로 반환</li>
     * </ol>
     *
     * @param userDetails 현재 인증된 사용자 정보 (CustomUserDetails)
     * @return 현재 로그인한 사용자의 UserResponseDto 응답
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<APIResponse<UserResponseDto>> getMe(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UserResponseDto data = userService.getUserInfo(userDetails.getUserId());

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.ok("회원정보를 불러오는데 성공했습니다.", data));
    }

    /**
     * 현재 로그인한 사용자의 프로필 정보를 수정한다.
     *
     * <p><b>처리 흐름:</b></p>
     * <ol>
     *   <li>Spring Security의 {@code @AuthenticationPrincipal}을 통해 인증된 사용자 정보(CustomUserDetails) 획득</li>
     *   <li>수정 요청 DTO({@link UserUpdateRequestDto})를 기반으로 {@code userService.updateUserInfo()} 호출</li>
     *   <li>엔티티 업데이트 후 {@link UserResponseDto}로 변환</li>
     *   <li>{@link APIResponse} 래핑을 통해 최신 사용자 정보 반환</li>
     * </ol>
     *
     * @param userDetails 현재 인증된 사용자 정보 (CustomUserDetails)
     * @param dto         수정 요청 DTO ({@link UserUpdateRequestDto})
     * @return 수정된 사용자 프로필 정보 ({@link UserResponseDto})를 담은 응답
     */
    @PatchMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<APIResponse<UserResponseDto>> updateMe(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UserUpdateRequestDto dto
    ) {
        UserResponseDto data = userService.updateUserInfo(userDetails.getUserId(), dto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.ok("회원정보를 수정하는데 성공했습니다.", data));
    }

    /**
     * 회원 탈퇴 API
     *
     * <p><b>처리 흐름:</b></p>
     * <ol>
     *   <li>인증된 사용자(@AuthenticationPrincipal) 정보를 가져옴</li>
     *   <li>요청 본문으로 전달된 {@link WithdrawRequestDto}에서 비밀번호를 검증</li>
     *   <li>서비스 계층 {@code userService.withdraw()} 호출 → 비밀번호 확인, 소프트 삭제, Refresh Token 제거</li>
     *   <li>추가적으로 응답 쿠키에서 Refresh Token을 만료 처리 (Max-Age=0)</li>
     *   <li>탈퇴 성공 메시지를 포함한 200 OK 응답 반환</li>
     * </ol>
     *
     * @param userDetails 인증된 사용자 정보 (Spring Security Principal)
     * @param dto         탈퇴 요청 DTO (비밀번호 포함)
     * @param response    HTTP 응답 객체 (쿠키 만료 처리용)
     * @return {@link APIResponse} 성공 메시지 (200 OK)
     */
    @DeleteMapping("/me/withdraw")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<APIResponse<Void>> withdraw(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody WithdrawRequestDto dto,
            HttpServletResponse response
    ) {
        userService.withdraw(userDetails.getUserId(), dto.password());

        addCookie(response, "", REFRESH_TOKEN_COOKIE_NAME, 0);

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.ok("회원탈퇴에 성공했습니다."));
    }

    /**
     * 특정 사용자가 <b>받은 리뷰 목록</b>을 페이지네이션 방식으로 조회한다.
     * TODO Page sort parameter 화이트리스트 구현
     *
     * <p><b>처리 흐름:</b></p>
     * <ol>
     *   <li>요청 경로의 {@code userId}로 대상 사용자 존재 여부를 확인한다. 존재하지 않을 경우 {@link BusinessException} 발생.</li>
     *   <li>Spring MVC가 요청 파라미터({@code page}, {@code size}, {@code sort})를 {@link Pageable} 객체로 자동 변환한다.</li>
     *   <li>{@code reviewService.getReviewsByRevieweeId()}를 호출해 해당 사용자가 받은 리뷰를 조회한다.</li>
     *   <li>컨트롤러는 이를 {@link APIResponse}로 감싸 200 OK 응답을 반환한다.</li>
     * </ol>
     *
     * <p><b>요청 파라미터 예시:</b></p>
     * <ul>
     *   <li>{@code GET /users/3/reviews/received?page=0&size=5&sort=createdAt,desc}</li>
     *   <li>페이지 번호는 0부터 시작 (Spring Data JPA의 기본 규칙)</li>
     * </ul>
     *
     * @param userId   리뷰 대상 사용자의 고유 ID (경로 변수)
     * @param pageable 페이징 및 정렬 정보 (기본값: size=5, sort=createdAt, direction=DESC)
     * @return 받은 리뷰 목록을 포함한 {@link APIResponse} (200 OK)
     * @throws BusinessException 대상 사용자가 존재하지 않을 경우 {@link ErrorCode#USER_NOT_FOUND} 발생
     */
    @GetMapping("/{userId}/reviews/received")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<APIResponse<Page<ReviewResponseDto>>> getReviews(
            @PathVariable(name = "userId") Long userId,
            @PageableDefault(
                    size = 5,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        if (!userService.existsById(userId)) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        Page<ReviewResponseDto> data = reviewService.getReviewsByRevieweeId(userId, pageable);

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.ok("리뷰 조회에 성공했습니다.", data));
    }

    /**
     * 현재 로그인한 사용자의 프로필 이미지를 업로드한다.
     *
     * @param userDetails 현재 인증된 사용자 정보
     * @param file        업로드할 이미지 파일
     * @return 업로드된 사용자 정보를 담은 응답
     */
    @PostMapping("/me/profile-image")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<APIResponse<UserResponseDto>> uploadProfileImage(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("file") MultipartFile file
    ) {
        UserResponseDto data = userService.uploadProfileImage(userDetails.getUserId(), file);

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.ok("프로필 이미지 업로드에 성공했습니다.", data));
    }

    /**
     * 현재 로그인한 사용자의 프로필 이미지를 삭제하고 기본 이미지로 변경한다.
     *
     * @param userDetails 현재 인증된 사용자 정보
     * @return 기본 이미지로 변경된 사용자 정보를 담은 응답
     */
    @DeleteMapping("/me/profile-image")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<APIResponse<UserResponseDto>> deleteProfileImage(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UserResponseDto data = userService.deleteProfileImage(userDetails.getUserId());

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.ok("프로필 이미지가 기본 이미지로 변경되었습니다.", data));
    }

    /**
     * 주어진 토큰을 응답 쿠키에 설정한다.
     *
     * <p>설정 옵션:</p>
     * <ul>
     *   <li><b>HttpOnly</b>: true (JS에서 접근 불가, XSS 방어)</li>
     *   <li><b>Secure</b>: true (HTTPS에서만 전송)</li>
     *   <li><b>SameSite</b>: None (기본 CSRF 방어)</li>
     *   <li><b>Path</b>: "/" (애플리케이션 전역에서 사용 가능)</li>
     *   <li><b>Max-Age</b>: 토큰 만료 시간(초)</li>
     * </ul>
     *
     * @param response   HTTP 응답
     * @param token      저장할 토큰 값
     * @param cookieName 쿠키 이름
     * @param maxAge     만료 시간(초)
     */
    private void addCookie(
            HttpServletResponse response,
            String token,
            String cookieName,
            long maxAge
    ) {
        ResponseCookie cookie = ResponseCookie.from(cookieName, token)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(maxAge)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }
}
