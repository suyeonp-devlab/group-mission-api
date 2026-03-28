CREATE TABLE members (
  id BIGSERIAL PRIMARY KEY,
  email TEXT NOT NULL UNIQUE,
  password TEXT NOT NULL,
  nickname VARCHAR(20) NOT NULL,
  profile_image_path TEXT NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  login_fail_count INT NOT NULL DEFAULT 0,
  password_changed_at TIMESTAMP NOT NULL DEFAULT NOW(),
  last_login_at TIMESTAMP NULL,
  last_checked_at TIMESTAMP NULL,
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMP NULL
);

COMMENT ON TABLE members IS '회원 정보 테이블';

COMMENT ON COLUMN members.id IS '회원 PK';
COMMENT ON COLUMN members.email IS '이메일 (인증 기반 로그인)';
COMMENT ON COLUMN members.password IS '비밀번호';
COMMENT ON COLUMN members.nickname IS '닉네임';
COMMENT ON COLUMN members.profile_image_path IS '프로필 이미지 상대경로';
COMMENT ON COLUMN members.status IS '회원 상태 (ACTIVE, INACTIVE, SUSPENDED)';
COMMENT ON COLUMN members.login_fail_count IS '로그인 실패 횟수';
COMMENT ON COLUMN members.password_changed_at IS '마지막 비밀번호 변경 시각';
COMMENT ON COLUMN members.last_login_at IS '마지막 로그인 성공 시각';
COMMENT ON COLUMN members.last_checked_at IS '사용자 인증 확인 시각';
COMMENT ON COLUMN members.created_at IS '생성 시각';
COMMENT ON COLUMN members.updated_at IS '수정 시각';