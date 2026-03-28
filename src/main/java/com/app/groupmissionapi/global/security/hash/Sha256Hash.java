package com.app.groupmissionapi.global.security.hash;

import com.app.groupmissionapi.global.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

import static com.app.groupmissionapi.global.exception.ErrorCode.INTERNAL_SERVER_ERROR;
import static java.nio.charset.StandardCharsets.*;

@Slf4j
@Component
public class Sha256Hash {

  private static final String ALGORITHM = "SHA-256";

  public String hash(String value) {

    if (!StringUtils.hasText(value)) {
      log.error("hash Exception=해시할 값이 비어 있습니다.");
      throw new CustomException(INTERNAL_SERVER_ERROR);
    }

    try {
      MessageDigest messageDigest = MessageDigest.getInstance(ALGORITHM);
      byte[] digest = messageDigest.digest(value.getBytes(UTF_8));
      return HexFormat.of().formatHex(digest);
    } catch (NoSuchAlgorithmException e) {
      log.error("hash Exception", e);
      throw new CustomException(INTERNAL_SERVER_ERROR);
    }
  }

  public boolean matches(String rawValue, String hashedValue) {

    if (!StringUtils.hasText(rawValue) || !StringUtils.hasText(hashedValue)) {
      return false;
    }

    return hash(rawValue).equals(hashedValue);
  }

}
