package com.app.groupmissionapi.domain.auth.service;

import com.app.groupmissionapi.domain.auth.dto.request.LoginRequest;
import com.app.groupmissionapi.domain.auth.dto.request.PasswordForgotResetRequest;
import com.app.groupmissionapi.domain.auth.dto.request.SignupRequest;
import com.app.groupmissionapi.domain.auth.dto.response.LoginResponse;
import com.app.groupmissionapi.domain.auth.repository.redis.PasswordForgotVerificationRepository;
import com.app.groupmissionapi.domain.auth.repository.redis.RefreshTokenRepository;
import com.app.groupmissionapi.domain.auth.repository.redis.SignupVerificationRepository;
import com.app.groupmissionapi.domain.member.entity.Member;
import com.app.groupmissionapi.domain.member.entity.MemberStatus;
import com.app.groupmissionapi.domain.member.repository.MemberRepository;
import com.app.groupmissionapi.global.constant.JwtConstants;
import com.app.groupmissionapi.global.exception.AuthException;
import com.app.groupmissionapi.global.exception.CustomException;
import com.app.groupmissionapi.global.file.FileDirectory;
import com.app.groupmissionapi.global.file.service.FileService;
import com.app.groupmissionapi.global.mail.service.MailService;
import com.app.groupmissionapi.global.security.hash.Sha256Hash;
import com.app.groupmissionapi.global.security.jwt.JwtProvider;
import com.app.groupmissionapi.global.util.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static com.app.groupmissionapi.global.exception.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

  private final PasswordEncoder passwordEncoder;
  private final JwtProvider jwtProvider;
  private final CookieUtil cookieUtil;
  private final Sha256Hash sha256Hash;
  private final MailService mailService;
  private final MemberRepository memberRepository;
  private final FileService fileService;
  private final RefreshTokenRepository refreshTokenRepository;
  private final SignupVerificationRepository signupVerificationRepository;
  private final PasswordForgotVerificationRepository passwordForgotVerificationRepository;

  private static final int CODE_FAIL_CNT = 5;
  private static final List<String> DEFAULT_PROFILE_IMAGES = List.of(
     "profile/default-profile(1).png"
    ,"profile/default-profile(2).png"
    ,"profile/default-profile(3).png"
    ,"profile/default-profile(4).png");

  /** 로그인 */
  @Transactional(noRollbackFor = AuthException.class)
  public LoginResponse login(LoginRequest request, HttpServletResponse httpResponse) {

    LocalDateTime now = LocalDateTime.now();

    // 이메일 조회
    Member member = memberRepository.findByEmail(request.getEmail())
      .orElseThrow(() -> new AuthException(EMAIL_OR_PASSWORD_INVALID));

    // 상태 확인
    if (member.getStatus() != MemberStatus.ACTIVE) {
      throw new AuthException(MEMBER_LOCKED);
    }

    // 비밀번호 오입력 횟수 확인
    if (member.isPasswordChangeRequired()) {
      throw new AuthException(PASSWORD_CHANGE_REQUIRED);
    }

    // 비밀번호 일치 여부 확인
    if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {

      member.increaseLoginFailCount();

      // 비밀번호 변경 유도
      if (member.isPasswordChangeRequired()) {
        throw new AuthException(PASSWORD_CHANGE_REQUIRED);
      }

      throw new AuthException(EMAIL_OR_PASSWORD_INVALID);
    }

    // 로그인 성공 처리
    member.successLogin(now);

    // token 발급 및 쿠키 저장
    String accessToken = jwtProvider.generateAccessToken(member.getId(), member.getEmail());
    String refreshToken = jwtProvider.generateRefreshToken(member.getId());
    String refreshTokenHash = sha256Hash.hash(refreshToken);

    cookieUtil.addCookie(
      httpResponse,
      JwtConstants.ACCESS_TOKEN_COOKIE_NAME,
      accessToken,
      (int) (jwtProvider.getAccessTokenExpirationMs() / 1000)
    );

    cookieUtil.addCookie(
      httpResponse,
      JwtConstants.REFRESH_TOKEN_COOKIE_NAME,
      refreshToken,
      (int) (jwtProvider.getRefreshTokenExpirationMs() / 1000)
    );

    // refresh token 저장
    refreshTokenRepository.saveToken(
      member.getId(),
      refreshTokenHash,
      jwtProvider.getRefreshTokenExpirationMs()
    );

    return new LoginResponse(
      member.getId(),
      member.getNickname(),
      fileService.buildFileUrl(member.getProfileImagePath())
    );
  }

  /** 회원가입 메일 인증코드 전송 */
  public void sendSignupCode(String email, String ip){

    // 가입여부
    if (memberRepository.existsByEmail(email)) {
      throw new CustomException(EMAIL_ALREADY_EXISTS);
    }

    // 메일 재전송 제한 - IP
    if (signupVerificationRepository.hasIpLimit(ip)) {
      throw new CustomException(TOO_MANY_REQUEST);
    }

    // 메일 재전송 제한 - 쿨다운
    if (signupVerificationRepository.hasCooldown(email)) {
      throw new CustomException(TOO_MANY_REQUEST);
    }

    // 메일 전송 후 code 저장
    String code = mailService.sendMailCode(email);
    String codeHash = sha256Hash.hash(code);

    signupVerificationRepository.saveCode(email, codeHash);

    log.info("sendSignupCode email code={}", code);

    // 실패 횟수 초기화 & 메일 재전송 제한 설정
    signupVerificationRepository.deleteFailCount(email);
    signupVerificationRepository.saveCooldown(email);
  }

  /** 회원가입 메일 인증코드 확인 */
  public void verifySignupCode(String email, String code) {

    // 인증 실패 횟수 초과 여부
    int failCount = signupVerificationRepository.findFailCount(email);
    if (failCount >= CODE_FAIL_CNT) {
      throw new CustomException(TOO_MANY_VERIFICATION_ATTEMPTS);
    }

    // 저장된 인증코드 조회
    String savedCode = signupVerificationRepository.findCode(email);
    if (savedCode == null) {
      throw new CustomException(CODE_EXPIRED);
    }

    // 인증코드 불일치
    if (!sha256Hash.matches(code, savedCode)) {
      signupVerificationRepository.increaseFailCount(email);
      throw new CustomException(CODE_INVALID);
    }

    // 인증 성공 처리 (redis 정리)
    signupVerificationRepository.deleteCode(email);
    signupVerificationRepository.deleteFailCount(email);
    signupVerificationRepository.deleteCooldown(email);
    signupVerificationRepository.saveVerified(email, savedCode);
  }

  /** 회원가입 */
  @Transactional
  public void signup(SignupRequest request, String ip) {

    LocalDateTime now = LocalDateTime.now();

    // 가입여부
    if (memberRepository.existsByEmail(request.getEmail())) {
      throw new CustomException(EMAIL_ALREADY_EXISTS);
    }

    // 비밀번호와 비밀번호확인 일치 여부
    if(!request.isPasswordMatched()) {
      throw new CustomException(PASSWORD_CONFIRM_NOT_MATCH);
    }

    // 이메일 인증 완료 여부
    String verifiedCode = signupVerificationRepository.findVerified(request.getEmail());
    if (verifiedCode == null) {
      throw new CustomException(EMAIL_NOT_VERIFIED);
    }

    // 프로필 이미지 처리
    String profileImagePath;

    if(!request.hasProfileImage()){
      profileImagePath = getRandomProfileImagePath();
    }else {
      profileImagePath = fileService.saveFile(request.getProfileImage(), FileDirectory.PROFILE);
    }

    // 회원가입
    Member member = Member.create(
      request.getEmail()
     ,passwordEncoder.encode(request.getPassword())
     ,request.getNickname()
     ,profileImagePath
     ,now);

    memberRepository.save(member);

    // 회원가입 성공 후처리 (redis 정리)
    signupVerificationRepository.deleteVerified(request.getEmail());
    signupVerificationRepository.saveIpLimit(ip);
  }

  /** 비밀번호 찾기 메일 인증코드 전송 */
  public void sendPasswordForgotCode(String email) {

    // 가입여부
    if (!memberRepository.existsByEmail(email)) {
      throw new CustomException(MEMBER_NOT_FOUND);
    }

    // 메일 재전송 제한 - 쿨다운
    if (passwordForgotVerificationRepository.hasCooldown(email)) {
      throw new CustomException(TOO_MANY_REQUEST);
    }

    // 메일 전송 후 code 저장
    String code = mailService.sendMailCode(email);
    String codeHash = sha256Hash.hash(code);

    passwordForgotVerificationRepository.saveCode(email, codeHash);

    log.info("sendPasswordForgotCode email code={}", code);

    // 실패 횟수 초기화 & 메일 재전송 제한 설정
    passwordForgotVerificationRepository.deleteFailCount(email);
    passwordForgotVerificationRepository.saveCooldown(email);
  }

  /** 비밀번호 찾기 메일 인증코드 확인 */
  public void verifyPasswordForgotCode(String email, String code) {

    // 인증 실패 횟수 초과 여부
    int failCount = passwordForgotVerificationRepository.findFailCount(email);
    if (failCount >= CODE_FAIL_CNT) {
      throw new CustomException(TOO_MANY_VERIFICATION_ATTEMPTS);
    }

    // 저장된 인증코드 조회
    String savedCode = passwordForgotVerificationRepository.findCode(email);
    if (savedCode == null) {
      throw new CustomException(CODE_EXPIRED);
    }

    // 인증코드 불일치
    if (!sha256Hash.matches(code, savedCode)) {
      passwordForgotVerificationRepository.increaseFailCount(email);
      throw new CustomException(CODE_INVALID);
    }

    // 인증 성공 처리 (redis 정리)
    passwordForgotVerificationRepository.deleteCode(email);
    passwordForgotVerificationRepository.deleteFailCount(email);
    passwordForgotVerificationRepository.deleteCooldown(email);
    passwordForgotVerificationRepository.saveVerified(email, savedCode);
  }

  /** 비밀번호 재설정 */
  @Transactional
  public void resetForgottenPassword(PasswordForgotResetRequest request) {

    LocalDateTime now = LocalDateTime.now();

    // 이메일 조회
    Member member = memberRepository.findByEmail(request.getEmail())
      .orElseThrow(() -> new AuthException(MEMBER_NOT_FOUND));

    // 비밀번호와 비밀번호확인 일치 여부
    if(!request.isPasswordMatched()) {
      throw new CustomException(PASSWORD_CONFIRM_NOT_MATCH);
    }

    // 이메일 인증 완료 여부
    String verifiedCode = passwordForgotVerificationRepository.findVerified(request.getEmail());
    if (verifiedCode == null) {
      throw new CustomException(EMAIL_NOT_VERIFIED);
    }

    // 이전 비밀번호와 일치 여부
    if (passwordEncoder.matches(request.getPassword(), member.getPassword())) {
      throw new CustomException(PASSWORD_SAME_AS_OLD);
    }

    // 비밀번호 변경
    member.changePassword(passwordEncoder.encode(request.getPassword()), now);

    // 비밀번호 변경 성공 후처리 (redis 정리)
    passwordForgotVerificationRepository.deleteVerified(request.getEmail());
    refreshTokenRepository.deleteToken(member.getId());
  }

  /** 랜덤 프로필 선택 */
  private String getRandomProfileImagePath() {
    int randomIndex = ThreadLocalRandom.current().nextInt(DEFAULT_PROFILE_IMAGES.size());
    return DEFAULT_PROFILE_IMAGES.get(randomIndex);
  }

}
