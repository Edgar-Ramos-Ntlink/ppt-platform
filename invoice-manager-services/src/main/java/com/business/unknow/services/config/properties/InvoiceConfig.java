package com.business.unknow.services.config.properties;

import java.io.Serializable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "invoicedatasource")
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Setter
@Getter
@ToString
public class InvoiceConfig implements Serializable {

  private static final long serialVersionUID = 8914903583724311162L;

  @Value("${invoicedatasource.springDatasourceUsername}")
  private String dataSourceUser;

  @Value("${invoicedatasource.springDatasourcePassword}")
  private String dataSourcePass;

  @Value("${invoicedatasource.springDatasourceDriverClassName}")
  private String dataSourceClassName;

  @Value("${invoicedatasource.springDatasourceUrl}")
  private String dataSourceUrl;

  @Value("${invoicedatasource.maximum-pool-size:2}")
  private Integer maxiumPoolSize;
}
