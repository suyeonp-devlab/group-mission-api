package com.app.groupmissionapi.domain.auth.repository.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import static com.app.groupmissionapi.global.redis.RedisKeys.*;
import static com.app.groupmissionapi.global.redis.RedisKeys.signupFailCountKey;
import static com.app.groupmissionapi.global.redis.RedisKeys.signupIpLimitKey;
import static com.app.groupmissionapi.global.redis.RedisTtl.*;

@Service
@RequiredArgsConstructor
public class SignupVerificationRepository {

  private final StringRedisTemplate stringRedisTemplate;

  public void saveCode(String email, String codeHash) {
    String key = signupCodeKey(email);
    stringRedisTemplate.opsForValue().set(key, codeHash, SIGNUP_CODE_TTL);
  }

  public String findCode(String email) {
    String key = signupCodeKey(email);
    return stringRedisTemplate.opsForValue().get(key);
  }

  public void deleteCode(String email) {
    String key = signupCodeKey(email);
    stringRedisTemplate.delete(key);
  }

  public void saveCooldown(String email) {
    String key = signupCooldownKey(email);
    stringRedisTemplate.opsForValue().set(key, "1", SIGNUP_COOLDOWN_TTL);
  }

  public boolean hasCooldown(String email) {
    String key = signupCooldownKey(email);
    return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key));
  }

  public void deleteCooldown(String email) {
    String key = signupCooldownKey(email);
    stringRedisTemplate.delete(key);
  }

  public void increaseFailCount(String email) {
    String key = signupFailCountKey(email);
    Long count = stringRedisTemplate.opsForValue().increment(key);

    // 최초 생성인 경우만 TTL 설정
    if (count != null && count == 1L) {
      stringRedisTemplate.expire(key, SIGNUP_FAIL_COUNT_TTL);
    }
  }

  public int findFailCount(String email) {
    String key = signupFailCountKey(email);
    String value = stringRedisTemplate.opsForValue().get(key);
    return (value != null) ? Integer.parseInt(value) : 0;
  }

  public void deleteFailCount(String email) {
    String key = signupFailCountKey(email);
    stringRedisTemplate.delete(key);
  }

  public void saveIpLimit(String ip) {
    String key = signupIpLimitKey(ip);
    stringRedisTemplate.opsForValue().set(key, "1", SIGNUP_IP_LIMIT_TTL);
  }

  public boolean hasIpLimit(String ip) {
    String key = signupIpLimitKey(ip);
    return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key));
  }

  public void saveVerified(String email, String codeHash) {
    String key = signupVerifiedKey(email);
    stringRedisTemplate.opsForValue().set(key, codeHash, SIGNUP_VERIFIED_TTL);
  }

  public String findVerified(String email) {
    String key = signupVerifiedKey(email);
    return stringRedisTemplate.opsForValue().get(key);
  }

  public void deleteVerified(String email) {
    String key = signupVerifiedKey(email);
    stringRedisTemplate.delete(key);
  }

}
