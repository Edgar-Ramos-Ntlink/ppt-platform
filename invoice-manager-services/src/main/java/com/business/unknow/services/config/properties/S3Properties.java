package com.business.unknow.services.config.properties;

import com.amazonaws.regions.Regions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "s3")
public class S3Properties {

  @Value("${s3.bucket.name}")
  private String bucketName;

  private final Regions region = Regions.US_WEST_1;

  public String getBucketName() {
    return bucketName;
  }

  public Regions getRegion() {
    return region;
  }
}
