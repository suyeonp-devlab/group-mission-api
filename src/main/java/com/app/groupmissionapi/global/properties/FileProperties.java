package com.app.groupmissionapi.global.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "file")
public class FileProperties {

  private String uploadPath;
  private String accessUrl;

}
