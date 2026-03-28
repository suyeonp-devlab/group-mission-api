package com.app.groupmissionapi.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
public enum ErrorCode {

  /** =====================================
   * 공통
   ========================================= */
  INVALID_INPUT_VALUE(BAD_REQUEST, "COMMON-400", "잘못된 요청입니다."),
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON-500", "서버 내부 오류가 발생했습니다."),
  UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON-401", "인증이 필요합니다."),
  ACCESS_DENIED(FORBIDDEN, "COMMON-403", "접근 권한이 없습니다."),
  TOO_MANY_REQUEST(TOO_MANY_REQUESTS, "COMMON-429", "요청이 너무 많습니다. 잠시 후 다시 시도해주세요."),

  /** =====================================
   * AUTH 관련
   ========================================= */
  EMAIL_OR_PASSWORD_INVALID(BAD_REQUEST, "AUTH-001", "이메일 또는 비밀번호가 올바르지 않습니다."),
  MEMBER_LOCKED(BAD_REQUEST, "AUTH-002", "잠긴 계정입니다."),
  PASSWORD_CHANGE_REQUIRED(BAD_REQUEST, "AUTH-003", "비밀번호 변경이 필요합니다."),
  EMAIL_ALREADY_EXISTS(BAD_REQUEST, "AUTH-004", "이미 가입된 이메일입니다."),
  CODE_INVALID(BAD_REQUEST, "AUTH-005", "인증번호가 올바르지 않습니다."),
  CODE_EXPIRED(BAD_REQUEST, "AUTH-006", "인증번호가 만료되었거나 존재하지 않습니다. 인증번호를 다시 요청해주세요."),
  TOO_MANY_VERIFICATION_ATTEMPTS(BAD_REQUEST, "AUTH-007", "인증 시도 횟수를 초과했습니다. 인증번호를 다시 요청해주세요."),
  PASSWORD_CONFIRM_NOT_MATCH(BAD_REQUEST, "AUTH-008", "비밀번호와 비밀번호 확인이 일치하지 않습니다."),
  EMAIL_NOT_VERIFIED(BAD_REQUEST, "AUTH-009", "이메일 인증이 완료되지 않았습니다."),
  PASSWORD_SAME_AS_OLD(BAD_REQUEST, "AUTH-010", "기존 비밀번호와 동일한 비밀번호는 사용할 수 없습니다."),

  /** =====================================
   * MEMBER 관련
   ========================================= */
  MEMBER_NOT_FOUND(NOT_FOUND, "MEMBER-001", "존재하지 않는 회원입니다."),

  /** =====================================
   * MAIL 관련
   ========================================= */
  MAIL_SEND_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "MAIL-001", "이메일 발송에 실패했습니다."),

  /** =====================================
   * FILE(IMAGE) 관련
   ========================================= */
  FILE_EMPTY(BAD_REQUEST, "FILE-001", "파일이 비어있습니다."),
  INVALID_FILE_FORMAT(BAD_REQUEST, "FILE-002", "지원하지 않는 파일 형식입니다."),
  INVALID_FILE_SIZE(BAD_REQUEST, "FILE-003", "파일 용량이 허용 범위를 초과했습니다."),
  FILE_UPLOAD_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "FILE-004", "파일 업로드 중 오류가 발생했습니다."),
  FILE_DELETE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "FILE-005", "파일 삭제 중 오류가 발생했습니다.");

  private final HttpStatus status;
  private final String code;
  private final String message;

  ErrorCode(HttpStatus status, String code, String message) {
    this.status = status;
    this.code = code;
    this.message = message;
  }

}
