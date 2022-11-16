package com.business.unknow.services.util;

import com.business.unknow.model.error.ErrorMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {

  @Override
  public boolean hasError(ClientHttpResponse httpResponse) throws IOException {

    return (httpResponse.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR
        || httpResponse.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR);
  }

  @Override
  public void handleError(ClientHttpResponse httpResponse) throws IOException {
    String messageBody =
        new BufferedReader(new InputStreamReader(httpResponse.getBody(), StandardCharsets.UTF_8))
            .lines()
            .collect(Collectors.joining("\n"));
    ErrorMessage error = new ObjectMapper().readValue(messageBody, ErrorMessage.class);
    log.error("External call error : [{}]", error);
    if (httpResponse.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error en el servidor");
    } else if (httpResponse.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR) {
      throw new ResponseStatusException(httpResponse.getStatusCode(), error.getDeveloperMessage());
    }
  }
}
