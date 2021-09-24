package com.business.unknow.services.client;

import com.business.unknow.client.RestNtlinkClientImpl;
import org.springframework.stereotype.Component;

@Component
public class NtlinkClient {

  public RestNtlinkClientImpl getNtlinkClient(String url, String context) {
    return new RestNtlinkClientImpl(url, context);
  }
}
