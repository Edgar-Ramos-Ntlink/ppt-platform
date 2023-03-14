package com.business.unknow.services.config;

import com.business.unknow.services.util.RestTemplateResponseErrorHandler;
import com.mx.ntlink.client.NtLinkClient;
import com.mx.ntlink.client.NtLinkClientImpl;
import com.unknown.aws.S3Utils;
import com.unknown.cfdi.mappers.CfdiMapper;
import com.unknown.cfdi.mappers.pagos.PagosMapper;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
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
  private String host;

  @Value("${ntlink.path}")
  private String path;

  @Value("${ntlink.timeout}")
  private Integer timeout;

  @Value("${ntlink.read-timeout}")
  private Integer readTimeout;

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
  public NtLinkClient getNtLinkClient() throws MalformedURLException {
    URL endpoint =
        new URL(
            new URL(host),
            path,
            new URLStreamHandler() {
              @Override
              protected URLConnection openConnection(URL url) throws IOException {
                URL target = new URL(url.toString());
                URLConnection connection = target.openConnection();
                connection.setConnectTimeout(timeout);
                connection.setReadTimeout(readTimeout);
                return (connection);
              }
            });
    return new NtLinkClientImpl(endpoint);
  }
}
