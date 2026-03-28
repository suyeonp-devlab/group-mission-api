package com.app.groupmissionapi.global.file;

import lombok.Getter;

import java.util.Set;

@Getter
public enum FileDirectory {

  PROFILE(
     "profile"
    ,2 * 1024 * 1024
    ,Set.of("jpg", "jpeg", "png", "webp")
    ,Set.of("image/jpg", "image/jpeg", "image/png", "image/webp")
  ),
  BANNER(
    "banner"
    ,5 * 1024 * 1024
    ,Set.of("jpg", "jpeg", "png", "webp")
    ,Set.of("image/jpg", "image/jpeg", "image/png", "image/webp")
  ),
  COMMUNITY(
    "community"
    ,2 * 1024 * 1024
    ,Set.of("jpg", "jpeg", "png", "webp")
    ,Set.of("image/jpg", "image/jpeg", "image/png", "image/webp")
  );

  private final String path;
  private final long maxSize;
  private final Set<String> allowedExts;
  private final Set<String> allowedContentTypes;

  FileDirectory(String path, long maxSize, Set<String> allowedExts, Set<String> allowedContentTypes) {
    this.path = path;
    this.maxSize = maxSize;
    this.allowedExts = allowedExts;
    this.allowedContentTypes = allowedContentTypes;
  }

}
