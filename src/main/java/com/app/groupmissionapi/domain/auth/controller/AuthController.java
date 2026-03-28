package com.app.groupmissionapi.domain.auth.controller;

import com.app.groupmissionapi.domain.auth.dto.request.LoginRequest;
import com.app.groupmissionapi.domain.auth.dto.request.SignupCodeRequest;
import com.app.groupmissionapi.domain.auth.dto.request.SignupRequest;
import com.app.groupmissionapi.domain.auth.dto.request.VerifySignupCodeRequest;
import com.app.groupmissionapi.domain.auth.dto.response.LoginResponse;
import com.app.groupmissionapi.domain.auth.service.AuthService;
import com.app.groupmissionapi.global.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import static com.app.groupmissionapi.global.util.WebUtil.getClientIp;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;

  /** 로그인 */
  @PostMapping("/login")
  public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request
                                         ,HttpServletResponse httpResponse) {

    LoginResponse result = authService.login(request, httpResponse);
    return ApiResponse.success(result);
  }

  /** 회원가입 메일 인증코드 전송 */
  @PostMapping("/signup/code")
  public ApiResponse<Void> sendSignupCode(@Valid @RequestBody SignupCodeRequest request
                                         ,HttpServletRequest httpRequest) {

    String ip = getClientIp(httpRequest);
    authService.sendSignupCode(request.getEmail(), ip);
    return ApiResponse.success(null);
  }

  /** 회원가입 메일 인증코드 확인 */
  @PostMapping("/signup/code/verify")
  public ApiResponse<Void> verifySignupCode(@Valid @RequestBody VerifySignupCodeRequest request) {

    authService.verifySignupCode(request.getEmail(), request.getCode());
    return ApiResponse.success(null);
  }

  /** 회원가입 */
  @PostMapping(value = "/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ApiResponse<Void> signup(@Valid @ModelAttribute("request") SignupRequest request
                                 ,HttpServletRequest httpRequest) {

    String ip = getClientIp(httpRequest);
    authService.signup(request, ip);
    return ApiResponse.success(null);
  }

}
