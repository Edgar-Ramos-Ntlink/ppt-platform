package com.business.unknow.services.config.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@ConfigurationProperties(prefix = "invoce")
public class GlocalConfigs {

  @Value("${invoce.environment}")
  private String environment;

  @Value("${invoce.email}")
  private String email;

  @Value("${invoce.email-pw}")
  private String emailPw;

  @Value("${invoce.email-host}")
  private String emailHost;

  @Value("${invoce.email-port}")
  private String emailPort;
}
