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

  public static String passwordForgotCodeKey(String email) {
    return "auth:password:forgot:code:" + email;
  }

  public static String passwordForgotFailCountKey(String email) {
    return "auth:password:forgot:fail:" + email;
  }

  public static String passwordForgotCooldownKey(String email) {
    return "auth:password:forgot:cooldown:" + email;
  }

  public static String passwordForgotVerifiedKey(String email) {
    return "auth:password:forgot:verified:" + email;
  }

}
