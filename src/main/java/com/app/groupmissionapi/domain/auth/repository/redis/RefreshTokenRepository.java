package com.app.groupmissionapi.domain.auth.repository.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

import static com.app.groupmissionapi.global.redis.RedisKeys.refreshTokenKey;

@Service
@RequiredArgsConstructor
public class RefreshTokenRepository {

  private final StringRedisTemplate stringRedisTemplate;

  public void saveToken(Long memberId, String refreshTokenHash, long expirationMs) {
    String key = refreshTokenKey(memberId);
    stringRedisTemplate.opsForValue().set(key, refreshTokenHash, Duration.ofMillis(expirationMs));
  }

  public String findToken(Long memberId) {
    String key = refreshTokenKey(memberId);
    return stringRedisTemplate.opsForValue().get(key);
  }

  public void deleteToken(Long memberId) {
    String key = refreshTokenKey(memberId);
    stringRedisTemplate.delete(key);
  }

}
