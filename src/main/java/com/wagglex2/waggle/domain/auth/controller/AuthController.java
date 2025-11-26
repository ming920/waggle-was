package com.wagglex2.waggle.domain.auth.controller;

import com.wagglex2.waggle.common.response.APIResponse;
import com.wagglex2.waggle.common.security.CustomUserDetails;
import com.wagglex2.waggle.common.security.jwt.JwtUtil;
import com.wagglex2.waggle.domain.auth.controller.docs.AuthControllerDocs;
import com.wagglex2.waggle.domain.auth.dto.request.EmailRequestDto;
import com.wagglex2.waggle.domain.auth.dto.request.EmailVerificationRequestDto;
import com.wagglex2.waggle.domain.auth.dto.request.SignInRequestDto;
import com.wagglex2.waggle.domain.auth.dto.request.SignUpRequestDto;
import com.wagglex2.waggle.domain.auth.dto.response.SignInResponseDto;
import com.wagglex2.waggle.domain.auth.dto.response.SignInResult;
import com.wagglex2.waggle.domain.auth.dto.response.TokenPair;
import com.wagglex2.waggle.domain.auth.service.AuthService;
import com.wagglex2.waggle.domain.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController implements AuthControllerDocs {

    private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";

    private final JwtUtil jwtUtil;
    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/email/code")
    public ResponseEntity<APIResponse<Void>> sendEmailAuthCode(
            @Valid @RequestBody EmailRequestDto dto
            ) {
        authService.sendAuthCode(dto.email());

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.ok("이메일 전송에 성공했습니다."));
    }


    @PostMapping("/email/verify")
    public ResponseEntity<APIResponse<Void>> verifyAuthCode(
            @Valid @RequestBody EmailVerificationRequestDto dto
    ) {
        authService.verifyCode(dto.email(), dto.inputCode());

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.ok("이메일 인증이 완료되었습니다."));
    }


    @PostMapping("/sign-up")
    public ResponseEntity<APIResponse<Long>> signUp(
            @Valid @RequestBody SignUpRequestDto dto
    ) {
        Long userId = userService.signUp(dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(APIResponse.ok("회원가입에 성공했습니다.", userId));
    }


    @PostMapping("/sign-in")
    public ResponseEntity<APIResponse<SignInResponseDto>> signIn(
            @Valid @RequestBody SignInRequestDto dto,
            HttpServletResponse response
    ) {

        // 1. 로그인 처리
        SignInResult signInResult = authService.login(dto);

        // 2. Access Token -> 헤더에 추가
        response.setHeader("Authorization", "Bearer " + signInResult.tokenPair().accessToken());

        // 3. Refresh Token -> 쿠키에 추가
        addCookie(response,
                signInResult.tokenPair().refreshToken(),
                REFRESH_TOKEN_COOKIE_NAME,
                jwtUtil.getRefreshExpMills() / 1000
        );

        SignInResponseDto signInResponseDto = new SignInResponseDto(
                signInResult.userId(),
                signInResult.username(),
                signInResult.status()
        );

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.ok("로그인에 성공했습니다.", signInResponseDto));
    }


    @PostMapping("/sign-out")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<APIResponse<Void>> signOut(HttpServletResponse response,
                                                     @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUserId();

        authService.deleteRefreshToken(userId);

        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, "")
                .maxAge(0)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        log.info("로그아웃 성공 : userId = {}", userId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.ok("로그아웃에 성공했습니다."));
    }


    @PostMapping("/refresh")
    public ResponseEntity<APIResponse<Void>> refreshToken(
            @CookieValue(name = REFRESH_TOKEN_COOKIE_NAME, required = false) String refreshToken,
            HttpServletResponse response
    ) {
        // 1. 토큰 재발급
        TokenPair tokens = authService.reissueTokens(refreshToken);

        // 2. Access Token -> 헤더에 추가
        response.setHeader("Authorization", "Bearer " + tokens.accessToken());

        // 2. Refresh Token -> 쿠키 설정
        addCookie(response,
                tokens.refreshToken(),
                REFRESH_TOKEN_COOKIE_NAME,
                jwtUtil.getRefreshExpMills() / 1000
        );

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.ok("토큰 재발급에 성공했습니다."));
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
