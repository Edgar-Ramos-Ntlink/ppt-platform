package com.business.unknow.services.config.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
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

  public String getEnvironment() {
    return environment;
  }

  public String getEmail() {
    return email;
  }

  public String getEmailPw() {
    return emailPw;
  }

  public String getEmailHost() {
    return emailHost;
  }

  public String getEmailPort() {
    return emailPort;
  }

  @Override
  public String toString() {
    return "GlocalConfigs [environment=" + environment + "]";
  }
}
