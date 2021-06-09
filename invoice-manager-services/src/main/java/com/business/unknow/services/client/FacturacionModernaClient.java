package com.business.unknow.services.client;

import com.business.unknow.client.FacturacionModernaClientImpl;
import com.business.unknow.client.interfaces.RestFacturacionModernaClient;
import org.springframework.stereotype.Component;

@Component
public class FacturacionModernaClient {

  public RestFacturacionModernaClient getFacturacionModernaClient(String url, String context) {
    return new FacturacionModernaClientImpl(url, context);
  }
}
