package com.app.groupmissionapi.global.file.service;

import com.app.groupmissionapi.global.file.FileDirectory;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

  String saveFile(MultipartFile file, FileDirectory directory);

  void deleteFile(String relativePath);

  String buildFileUrl(String relativePath);

}
