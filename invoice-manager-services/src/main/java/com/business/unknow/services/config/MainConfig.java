package com.business.unknow.services.config;

import com.business.unknow.services.util.RestTemplateResponseErrorHandler;
import com.mx.ntlink.client.NtLinkClient;
import com.mx.ntlink.client.NtLinkClientImpl;
import com.unknown.aws.S3Utils;
import com.unknown.cfdi.mappers.CfdiMapper;
import com.unknown.cfdi.mappers.pagos.PagosMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class MainConfig {

  @Value("${aws.region}")
  private String awsRegion;

  @Value("${ntlink.host}")
  private String ntlinkWsUrl;

  @Bean
  public RestTemplate template() {
    RestTemplate restTemplate = new RestTemplate();
    restTemplate.setErrorHandler(new RestTemplateResponseErrorHandler());
    return restTemplate;
  }

  @Bean
  public S3Utils getS3Utils() {
    return new S3Utils(awsRegion);
  }

  @Bean
  public CfdiMapper getCfidMapper() {
    return Mappers.getMapper(CfdiMapper.class);
  }

  @Bean
  public PagosMapper getPagosMapper() {
    return Mappers.getMapper(PagosMapper.class);
  }

  @Bean
  public NtLinkClient getNtLinkClient() {
    return new NtLinkClientImpl(ntlinkWsUrl);
  }
}
