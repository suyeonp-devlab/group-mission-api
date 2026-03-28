package com.app.groupmissionapi.global.redis;

import java.time.Duration;

public final class RedisTtl {

  private RedisTtl() {}

  /** =====================================
   * AUTH 관련
   ========================================= */
  public static final Duration SIGNUP_CODE_TTL = Duration.ofMinutes(5);

  public static final Duration SIGNUP_FAIL_COUNT_TTL = Duration.ofMinutes(5);

  public static final Duration SIGNUP_COOLDOWN_TTL = Duration.ofSeconds(60);

  public static final Duration SIGNUP_VERIFIED_TTL = Duration.ofMinutes(30);

  public static final Duration SIGNUP_IP_LIMIT_TTL = Duration.ofSeconds(10);

  public static final Duration PASSWORD_FORGOT_CODE_TTL = Duration.ofMinutes(5);

  public static final Duration PASSWORD_FORGOT_FAIL_COUNT_TTL = Duration.ofMinutes(5);

  public static final Duration PASSWORD_FORGOT_COOLDOWN_TTL = Duration.ofSeconds(60);

  public static final Duration PASSWORD_FORGOT_VERIFIED_TTL = Duration.ofMinutes(10);

}
