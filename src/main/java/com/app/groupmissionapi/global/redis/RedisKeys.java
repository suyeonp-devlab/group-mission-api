package com.app.groupmissionapi.global.redis;

public final class RedisKeys {

  private RedisKeys() {}

  /** =====================================
   * AUTH 관련
  ========================================= */
  public static String refreshTokenKey(Long memberId){
    return "auth:refresh:" + memberId;
  }

  public static String signupCodeKey(String email) {
    return "auth:signup:code:" + email;
  }

  public static String signupFailCountKey(String email) {
    return "auth:signup:fail:" + email;
  }

  public static String signupCooldownKey(String email) {
    return "auth:signup:cooldown:" + email;
  }

  public static String signupVerifiedKey(String email) {
    return "auth:signup:verified:" + email;
  }

  public static String signupIpLimitKey(String ip) {
    return "auth:signup:ip:" + ip;
  }

}
