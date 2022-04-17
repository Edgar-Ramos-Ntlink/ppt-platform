package com.business.unknow.services.config;

import com.mx.ntlink.aws.S3Utils;
import com.mx.ntlink.cfdi.mappers.CfdiMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UtilitiesConfig {

  @Value("${aws.region}")
  private String awsRegion;

  @Bean
  public S3Utils getS3Utils() {
    return new S3Utils(awsRegion);
  }

  @Bean
  public CfdiMapper getCfidMapper() {
    return Mappers.getMapper(CfdiMapper.class);
  }
}
