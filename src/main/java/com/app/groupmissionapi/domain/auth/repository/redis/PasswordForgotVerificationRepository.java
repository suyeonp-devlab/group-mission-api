package com.app.groupmissionapi.domain.auth.repository.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import static com.app.groupmissionapi.global.redis.RedisKeys.*;
import static com.app.groupmissionapi.global.redis.RedisTtl.*;

@Service
@RequiredArgsConstructor
public class PasswordForgotVerificationRepository {

  private final StringRedisTemplate stringRedisTemplate;

  public void saveCode(String email, String codeHash) {
    String key = passwordForgotCodeKey(email);
    stringRedisTemplate.opsForValue().set(key, codeHash, PASSWORD_FORGOT_CODE_TTL);
  }

  public String findCode(String email) {
    String key = passwordForgotCodeKey(email);
    return stringRedisTemplate.opsForValue().get(key);
  }

  public void deleteCode(String email) {
    String key = passwordForgotCodeKey(email);
    stringRedisTemplate.delete(key);
  }

  public void saveCooldown(String email) {
    String key = passwordForgotCooldownKey(email);
    stringRedisTemplate.opsForValue().set(key, "1", PASSWORD_FORGOT_COOLDOWN_TTL);
  }

  public boolean hasCooldown(String email) {
    String key = passwordForgotCooldownKey(email);
    return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key));
  }

  public void deleteCooldown(String email) {
    String key = passwordForgotCooldownKey(email);
    stringRedisTemplate.delete(key);
  }

  public void increaseFailCount(String email) {
    String key = passwordForgotFailCountKey(email);
    Long count = stringRedisTemplate.opsForValue().increment(key);

    // 최초 생성인 경우만 TTL 설정
    if (count != null && count == 1L) {
      stringRedisTemplate.expire(key, PASSWORD_FORGOT_FAIL_COUNT_TTL);
    }
  }

  public int findFailCount(String email) {
    String key = passwordForgotFailCountKey(email);
    String value = stringRedisTemplate.opsForValue().get(key);
    return (value != null) ? Integer.parseInt(value) : 0;
  }

  public void deleteFailCount(String email) {
    String key = passwordForgotFailCountKey(email);
    stringRedisTemplate.delete(key);
  }

  public void saveVerified(String email, String codeHash) {
    String key = passwordForgotVerifiedKey(email);
    stringRedisTemplate.opsForValue().set(key, codeHash, PASSWORD_FORGOT_VERIFIED_TTL);
  }

  public String findVerified(String email) {
    String key = passwordForgotVerifiedKey(email);
    return stringRedisTemplate.opsForValue().get(key);
  }

  public void deleteVerified(String email) {
    String key = passwordForgotVerifiedKey(email);
    stringRedisTemplate.delete(key);
  }

}
