package com.business.unknow.services.config;

import com.mx.ntlink.aws.S3Utils;
import com.mx.ntlink.cfdi.mappers.CfdiMapper;
import com.mx.ntlink.cfdi.mappers.pagos.PagosMapper;
import com.mx.ntlink.client.NtLinkClient;
import com.mx.ntlink.client.NtLinkClientImpl;
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
    return new RestTemplate();
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
