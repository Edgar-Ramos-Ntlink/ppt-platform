package com.business.unknow.services.config.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "sw")
@Setter
@Getter
@ToString
public class SwProperties {

  @Value("${sw.username}")
  private String user;

  @Value("${sw.password}")
  private String password;

  @Value("${sw.host}")
  private String host;

  @Value("${sw.context}")
  private String context;
}
