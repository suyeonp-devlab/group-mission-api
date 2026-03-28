package com.app.groupmissionapi.global.file.service;

import com.app.groupmissionapi.global.exception.CustomException;
import com.app.groupmissionapi.global.file.FileDirectory;
import com.app.groupmissionapi.global.properties.FileProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.app.groupmissionapi.global.exception.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocalFileService implements FileService {

  private final FileProperties fileProperties;

  /** 파일 저장 */
  @Override
  public String saveFile(MultipartFile file, FileDirectory directory) {

    if (file == null || file.isEmpty()) {
      throw new CustomException(FILE_EMPTY);
    }

    // 파일 용량
    if (file.getSize() > directory.getMaxSize()) {
      long maxSizeMB = directory.getMaxSize() * 1024 * 1024;
      throw new CustomException(INVALID_FILE_SIZE, "파일 용량이 허용 범위를 초과했습니다. [최대: " + maxSizeMB + "]");
    }

    // 파일 형식
    String contentType = file.getContentType();
    String ext = extractExt(file.getOriginalFilename()).orElse(null);
    Set<String> allowedExts = directory.getAllowedExts();
    Set<String> allowedContentTypes = directory.getAllowedContentTypes();

    if (contentType == null ||
        ext == null ||
        !allowedContentTypes.contains(contentType.toLowerCase()) ||
        !allowedExts.contains(ext.toLowerCase())) {
      throw new CustomException(INVALID_FILE_FORMAT);
    }

    // 저장용 파일명
    String fileName = UUID.randomUUID() + "." + ext;
    String relativePath = directory.getPath() + "/" + fileName;

    try {
      // 파일 저장
      Path targetPath = Paths.get(fileProperties.getUploadPath(), relativePath);
      Files.createDirectories(targetPath.getParent());

      Files.copy(file.getInputStream(), targetPath);

    } catch (IOException e) {
      log.error("saveFile Exception", e);
      throw new CustomException(FILE_UPLOAD_FAIL);
    }

    return relativePath;
  }

  /** 파일 삭제 */
  @Override
  public void deleteFile(String relativePath) {

    if (!StringUtils.hasText(relativePath)) return;

    try {
      // 파일 삭제
      Path targetPath = Paths.get(fileProperties.getUploadPath(), relativePath);
      Files.deleteIfExists(targetPath);

    } catch (IOException e) {
      log.error("deleteFile Exception", e);
      throw new CustomException(FILE_DELETE_FAIL);
    }
  }

  /** 파일 접근 경로 */
  public String buildFileUrl(String relativePath) {

    if (!StringUtils.hasText(relativePath)) {
      return null;
    }

    return fileProperties.getAccessUrl() + "/" + relativePath;
  }

  /** 파일 확장자 추출 */
  private Optional<String> extractExt(String fileName) {

    String ext = null;

    if (StringUtils.hasText(fileName) && fileName.contains(".")) {
      int startIdx = fileName.lastIndexOf('.') + 1;
      ext = fileName.substring(startIdx);
    }

    return Optional.ofNullable(ext);
  }

}
