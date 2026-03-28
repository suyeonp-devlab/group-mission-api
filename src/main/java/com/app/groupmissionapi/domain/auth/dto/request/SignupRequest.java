package com.app.groupmissionapi.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
public class SignupRequest {

  @NotBlank(message = "이메일은 필수입니다.")
  @Email(message = "이메일 형식이 올바르지 않습니다.")
  private String email;

  @NotBlank(message = "비밀번호는 필수입니다.")
  @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
  private String password;

  @NotBlank(message = "비밀번호 확인은 필수입니다.")
  private String passwordConfirm;

  @NotBlank(message = "닉네임은 필수입니다.")
  @Size(max = 10, message = "닉네임은 10자 이하로 입력해주세요.")
  private String nickname;

  private MultipartFile profileImage;

  public boolean isPasswordMatched() {
    return password != null && password.equals(passwordConfirm);
  }

  public boolean hasProfileImage() {
    return profileImage != null && !profileImage.isEmpty();
  }

}
