package com.business.unknow.services.config.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@ToString
@Configuration
@ConfigurationProperties(prefix = "fm")
public class FacturacionModernaProperties {

  @Value("${fm.username}")
  private String user;

  @Value("${fm.password}")
  private String password;

  @Value("${fm.host}")
  private String host;

  @Value("${fm.context}")
  private String context;
}
