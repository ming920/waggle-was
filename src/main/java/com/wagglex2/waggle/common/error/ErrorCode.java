package com.wagglex2.waggle.common.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 400
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", "잘못된 요청입니다."),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", "요청 값이 유효하지 않습니다."),
    REQUIRED_FIELD_MISSING(HttpStatus.BAD_REQUEST, "REQUIRED_FIELD_MISSING", "필수 값이 누락되었습니다."),
    INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "INVALID_EMAIL_FORMAT", "이메일 형식이 올바르지 않습니다."),
    INVALID_NICKNAME_FORMAT(HttpStatus.BAD_REQUEST, "INVALID_NICKNAME_FORMAT", "닉네임 형식이 올바르지 않습니다."),
    INVALID_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST, "INVALID_PASSWORD_FORMAT", "비밀번호 형식이 올바르지 않습니다."),
    MISMATCHED_PASSWORD(HttpStatus.BAD_REQUEST, "MISMATCHED_PASSWORD", "비밀번호와 비밀번호 확인이 일치하지 않습니다."),
    PASSWORD_SAME_AS_OLD(HttpStatus.BAD_REQUEST, "PASSWORD_SAME_AS_OLD", "기존 비밀번호와 새로운 비밀번호가 일치합니다."),
    OLD_PASSWORD_INCORRECT(HttpStatus.BAD_REQUEST, "OLD_PASSWORD_INCORRECT", "기존 비밀번호가 일치하지 않습니다."),
    UNSUPPORTED_UNIVERSITY_DOMAIN(HttpStatus.BAD_REQUEST, "UNSUPPORTED_UNIVERSITY_DOMAIN", "지원하지 않는 학교 도메인입니다."),
    VERIFICATION_CODE_EXPIRED(HttpStatus.BAD_REQUEST, "VERIFICATION_CODE_EXPIRED", "인증번호가 만료되었습니다."),
    INVALID_VERIFICATION_CODE(HttpStatus.BAD_REQUEST, "INVALID_VERIFICATION_CODE", "인증번호가 일치하지 않습니다."),
    INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST, "INVALID_DATE_RANGE", "유효하지 않은 날짜 범위입니다."),
    MAX_PARTICIPANTS_EXCEEDED(HttpStatus.BAD_REQUEST, "MAX_PARTICIPANTS_EXCEEDED", "참가 인원이 최대 모집 인원을 초과했습니다."),
    SELF_REVIEW_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "SELF_REVIEW_NOT_ALLOWED", "자기 자신에 대한 리뷰는 작성할 수 없습니다."),
    INVALID_PAGE_NUMBER(HttpStatus.BAD_REQUEST, "INVALID_PAGE_NUMBER", "페이지 번호는 1 이상이어야 합니다"),
    INVALID_ENUM_VALUE(HttpStatus.BAD_REQUEST, "INVALID_ENUM_VALUE", "쿼리 파라미터 값이 유효하지 않습니다. 허용 가능한 값 목록을 확인해주세요."),
    MISMATCHED_RECRUITMENT_CATEGORY(HttpStatus.BAD_REQUEST, "MISMATCHED_RECRUITMENT_CATEGORY", "지원하려는 공고의 카테고리가 요청한 카테고리와 일치하지 않습니다."),
    INVALID_SORT_PROPERTY(HttpStatus.BAD_REQUEST, "INVALID_SORT_PROPERTY", "잘못된 정렬 기준입니다."),
    INVALID_SORT_DIRECTION(HttpStatus.BAD_REQUEST, "INVALID_SORT_DIRECTION", "정렬 방향은 asc 또는 desc만 가능합니다."),
    PAGE_SIZE_OUT_OF_RANGE(HttpStatus.BAD_REQUEST, "PAGE_SIZE_OUT_OF_RANGE", "페이지 크기는 1 이상이어야 합니다."),
    PAGE_INDEX_OUT_OF_RANGE(HttpStatus.BAD_REQUEST, "PAGE_INDEX_OUT_OF_RANGE", "페이지 번호는 0 이상이어야 합니다."),
    INVALID_MEMBER_COUNT(HttpStatus.BAD_REQUEST, "INVALID_MEMBER_COUNT", "현재 인원이 0명 이하일 때는 감소할 수 없습니다."),
    FILE_NOT_UPLOADED(HttpStatus.BAD_REQUEST, "FILE_NOT_UPLOADED", "파일이 업로드되지 않았습니다."),
    INVALID_FILE_NAME(HttpStatus.BAD_REQUEST, "INVALID_FILE_NAME", "파일명이 올바르지 않습니다."),
    INVALID_FILE_FORMAT(HttpStatus.BAD_REQUEST, "INVALID_FILE_FORMAT", "지원하지 않는 파일 형식입니다. jpg, jpeg, png만 가능합니다."),
    USER_ALREADY_COMPLETED_BASIC_INFO(HttpStatus.BAD_REQUEST, "USER_ALREADY_COMPLETED_BASIC_INFO", "이미 기본 정보를 입력한 사용자입니다."),
    INVALID_ARGUMENT(HttpStatus.BAD_REQUEST, "INVALID_ARGUMENT", "유효하지 않은 인자 값입니다."),

    // 401
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "인증이 필요합니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", "아이디 또는 비밀번호가 올바르지 않습니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "REFRESH_TOKEN_EXPIRED", "리프레시 토큰이 만료되었습니다."),
    REFRESH_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "REFRESH_TOKEN_INVALID", "유효하지 않은 리프레시 토큰입니다."),
    REFRESH_TOKEN_TYPE_INVALID(HttpStatus.UNAUTHORIZED, "REFRESH_TOKEN_TYPE_INVALID", "리프레시 토큰 타입이 일치하지 않습니다."),
    REFRESH_TOKEN_MISMATCH(HttpStatus.UNAUTHORIZED, "REFRESH_TOKEN_MISMATCH", "리프레시 토큰이 일치하지 않습니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "REFRESH_TOKEN_NOT_FOUND", "리프레시 토큰을 찾을 수 없습니다."),
    ACCESS_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "ACCESS_TOKEN_INVALID", "유효하지 않은 액세스 토큰입니다."),

    // 403
    FORBIDDEN(HttpStatus.FORBIDDEN, "FORBIDDEN", "접근 권한이 없습니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "ACCESS_DENIED", "요청한 리소스에 접근할 수 없습니다."),
    CANNOT_UPDATE_NOT_ACTIVE_REVIEW(HttpStatus.FORBIDDEN, "CANNOT_UPDATE_NOT_ACTIVE_REVIEW","비활성화된 리뷰는 수정할 수 없습니다."),
    CANNOT_DELETE_NOT_ACTIVE_REVIEW(HttpStatus.FORBIDDEN, "CANNOT_DELETE_NOT_ACTIVE_REVIEW","비활성화된 리뷰는 삭제할 수 없습니다."),
    CANNOT_UPDATE_ANOTHER_USER_REVIEW(HttpStatus.FORBIDDEN, "CANNOT_UPDATE_ANOTHER_USER_REVIEW", "본인이 작성한 리뷰만 수정할 수 있습니다."),
    CANNOT_DELETE_ANOTHER_USER_REVIEW(HttpStatus.FORBIDDEN, "CANNOT_DELETE_ANOTHER_USER_REVIEW", "본인 리뷰만 삭제할 수 있습니다."),
    CANNOT_DELETE_ANOTHER_USER_BOOKMARK(HttpStatus.FORBIDDEN, "CANNOT_DELETE_ANOTHER_USER_BOOKMARK", "다른 사용자의 찜은 취소할 수 없습니다."),
    CANNOT_UPDATE_ANOTHER_USER_PROJECT(HttpStatus.FORBIDDEN, "CANNOT_UPDATE_ANOTHER_USER_PROJECT", "다른 사용자의 프로젝트 공고는 수정할 수 없습니다."),
    CANNOT_DELETE_ANOTHER_USER_PROJECT(HttpStatus.FORBIDDEN, "CANNOT_DELETE_ANOTHER_USER_PROJECT", "다른 사용자의 프로젝트 공고는 삭제할 수 없습니다."),
    CANNOT_DELETE_ANOTHER_USER_ASSIGNMENT(HttpStatus.FORBIDDEN, "CANNOT_DELETE_ANOTHER_USER_ASSIGNMENT", "다른 사용자의 과제는 삭제할 수 없습니다."),
    CANNOT_APPLY_OWN_RECRUITMENT(HttpStatus.FORBIDDEN, "CANNOT_APPLY_OWN_RECRUITMENT", "본인 공고에는 지원할 수 없습니다."),
    FORBIDDEN_CROSS_UNIVERSITY_RECRUITMENT(HttpStatus.FORBIDDEN, "FORBIDDEN_CROSS_UNIVERSITY_RECRUITMENT", "타 대학의 공고입니다."),
    FORBIDDEN_DECIDE_APPLICATION(HttpStatus.FORBIDDEN, "FORBIDDEN_DECIDE_APPLICATION", "지원 수락/거절 권한이 없습니다."),
    CANNOT_DELETE_ANOTHER_USER_APPLICATION(HttpStatus.FORBIDDEN, "CANNOT_DELETE_ANOTHER_USER_APPLICATION", "다른 사용자의 지원은 취소/삭제할 수 없습니다."),
    CANNOT_REMOVE_NOT_LEADER(HttpStatus.FORBIDDEN, "CANNOT_DELETE_NOT_LEADER", "리더만 멤버를 삭제할 수 있습니다."),
    CANNOT_REMOVE_SELF(HttpStatus.FORBIDDEN, "CANNOT_DELETE_MYSELF", "자기 자신은 삭제할 수 없습니다."),
    CANNOT_DELETE_ANOTHER_USER_NOTIFICATION(HttpStatus.FORBIDDEN, "CANNOT_DELETE_ANOTHER_USER_NOTIFICATION", "다른 사용자의 알림은 삭제할 수 없습니다."),

    // 404
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "사용자를 찾을 수 없습니다."),
    IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "IMAGE_NOT_FOUND", "파일을 찾을 수 없습니다."),
    RECRUITMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "RECRUITMENT_NOT_FOUND", "공고를 찾을 수 없습니다."),
    PROJECT_NOT_FOUND(HttpStatus.NOT_FOUND, "PROJECT_NOT_FOUND", "프로젝트 공고를 찾을 수 없습니다."),
    ASSIGNMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "ASSIGNMENT_NOT_FOUND", "과제 공고를 찾을 수 없습니다."),
    STUDY_NOT_FOUND(HttpStatus.NOT_FOUND, "STUDY_NOT_FOUND", "스터디 공고를 찾을 수 없습니다."),
    POSITION_NOT_FOUND(HttpStatus.NOT_FOUND, "POSITION_NOT_FOUND", "역할을 찾을 수 없습니다."),
    SKILL_NOT_FOUND(HttpStatus.NOT_FOUND, "SKILL_NOT_FOUND", "기술 스택을 찾을 수 없습니다."),
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "REVIEW_NOT_FOUND", "리뷰를 찾을 수 없습니다."),
    BOOKMARK_NOT_FOUND(HttpStatus.NOT_FOUND, "BOOKMARK_NOT_FOUND", "찜 정보를 찾을 수 없습니다."),
    APPLICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "APPLICATION_NOT_FOUND", "지원 정보를 찾을 수 없습니다."),
    TEAM_NOT_FOUND(HttpStatus.NOT_FOUND, "TEAM_NOT_FOUND", "팀을 찾을 수 없습니다."),
    LEADER_NOT_FOUND(HttpStatus.NOT_FOUND, "LEADER_NOT_FOUND", "리더를 찾을 수 없습니다."),
    TARGET_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "TARGET_MEMBER_NOT_FOUND", "삭제할 멤버를 찾을 수 없습니다."),
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTIFICATION_NOT_FOUND", "알림을 찾을 수 없습니다."),

    // 405
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED", "허용되지 않은 HTTP 메서드입니다."),

    // 406
    NOT_ACCEPTABLE(HttpStatus.NOT_ACCEPTABLE, "NOT_ACCEPTABLE", "응답 가능한 미디어 타입이 없습니다."),

    // 409
    DUPLICATED_USERNAME(HttpStatus.CONFLICT, "DUPLICATED_USERNAME", "이미 가입된 아이디입니다."),
    DUPLICATED_EMAIL(HttpStatus.CONFLICT, "DUPLICATED_EMAIL", "이미 가입된 이메일입니다."),
    DUPLICATED_NICKNAME(HttpStatus.CONFLICT, "DUPLICATED_NICKNAME", "이미 존재하는 닉네임입니다."),
    ALREADY_WITHDRAWN_USER(HttpStatus.CONFLICT, "ALREADY_WITHDRAWN_USER", "이미 탈퇴한 회원입니다."),
    ALREADY_BOOKMARKED(HttpStatus.CONFLICT, "ALREADY_BOOKMARKED", "이미 찜한 공고입니다."),
    ALREADY_APPLIED_RECRUITMENT(HttpStatus.CONFLICT, "ALREADY_APPLIED_RECRUITMENT", "이미 지원한 공고입니다."),
    RECRUITMENT_CLOSED(HttpStatus.CONFLICT, "RECRUITMENT_CLOSED", "모집 기간이 종료된 공고입니다."),
    RECRUITMENT_FULL(HttpStatus.CONFLICT, "RECRUITMENT_FULL", "이미 모집이 완료된 공고입니다."),
    NOT_RECRUITING_POSITION(HttpStatus.CONFLICT, "NOT_RECRUITING_POSITION", "해당 포지션은 모집 대상이 아닙니다."),
    POSITION_FULL(HttpStatus.CONFLICT, "POSITION_FULL", "이미 모집이 완료된 포지션입니다."),
    ALREADY_PROCESSED_APPLICATION(HttpStatus.CONFLICT, "ALREADY_PROCESSED_APPLICATION", "이미 처리된 지원서입니다."),
    TEAM_FULL(HttpStatus.CONFLICT, "TEAM_FULL", "이미 모집이 완료되었습니다."),

    // 413
    PAYLOAD_TOO_LARGE(HttpStatus.PAYLOAD_TOO_LARGE, "PAYLOAD_TOO_LARGE", "요청 또는 파일 크기가 너무 큽니다."),
    FILE_SIZE_TOO_LARGE(HttpStatus.PAYLOAD_TOO_LARGE, "FILE_SIZE_TOO_LARGE", "이미지 크기는 5MB를 초과하면 안됩니다."),

    // 415
    UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "UNSUPPORTED_MEDIA_TYPE", "지원하지 않는 Content-Type 입니다."),

    // 429
    TOO_MANY_REQUESTS(HttpStatus.TOO_MANY_REQUESTS, "TOO_MANY_REQUESTS", "요청이 너무 많습니다. 잠시 후 다시 시도해주세요."),

    // 500
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "서버 오류가 발생했습니다."),
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "DATABASE_ERROR", "데이터베이스 오류가 발생했습니다."),
    REDIS_CONNECTION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "REDIS_CONNECTION_ERROR", "Redis 연결에 실패했습니다."),
    EMAIL_CREATE_MESSAGE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "EMAIL_CREATE_MESSAGE_FAILED", "이메일 메시지를 생성하는데 실패했습니다"),
    EMAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "EMAIL_SEND_FAILED", "이메일 발송에 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
