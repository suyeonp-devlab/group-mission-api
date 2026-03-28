package com.app.groupmissionapi.domain.member.entity;

import com.app.groupmissionapi.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import static lombok.AccessLevel.*;

@Entity
@Table(name = "members")
@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class Member extends BaseTimeEntity {

  private static final int PASSWORD_FAIL_CNT = 5;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // 이메일
  @Column(nullable = false, unique = true)
  private String email;

  // 비밀번호
  @Column(nullable = false)
  private String password;

  // 닉네임
  @Column(nullable = false, length = 20)
  private String nickname;

  // 프로필 이미지 파일 경로
  @Column(nullable = false)
  private String profileImagePath;

  // 상태 (ACTIVE, INACTIVE, SUSPENDED 등)
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private MemberStatus status;

  // 로그인 실패 횟수
  @Column(nullable = false)
  private int loginFailCount;

  // 비밀번호 변경 시간
  @Column(nullable = false)
  private LocalDateTime passwordChangedAt;

  // 마지막 로그인 시간
  private LocalDateTime lastLoginAt;

  // 마지막 확인 시간 (알림 등)
  private LocalDateTime lastCheckedAt;

  /** 비밀번호 실패 처리 */
  public void increaseLoginFailCount() {
    this.loginFailCount++;
  }

  /** 비밀번호 변경 강제 여부 */
  public boolean isPasswordChangeRequired() {
    return this.loginFailCount >= PASSWORD_FAIL_CNT;
  }

  /** 로그인 성공 처리 */
  public void successLogin(LocalDateTime now) {
    this.loginFailCount = 0;
    this.lastLoginAt = now;
  }

  /** 회원 생성 */
  public static Member create(String email
                             ,String encodedPassword
                             ,String nickname
                             ,String profileImagePath
                             ,LocalDateTime now) {

    return Member.builder()
      .email(email)
      .password(encodedPassword)
      .nickname(nickname)
      .profileImagePath(profileImagePath)
      .status(MemberStatus.ACTIVE)
      .loginFailCount(0)
      .passwordChangedAt(now)
      .build();
  }

  /** 비밀번호 변경 */
  public void changePassword(String encodedPassword, LocalDateTime now) {
    this.password = encodedPassword;
    this.loginFailCount = 0;
    this.passwordChangedAt = now;
  }

}
