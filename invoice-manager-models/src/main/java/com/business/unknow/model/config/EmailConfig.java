package com.business.unknow.model.config;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Setter
@Getter
public class EmailConfig {

  private List<String> receptor;
  private String emisor;
  private String dominio;
  private String asunto;
  private String cuerpo;
  private String pwEmisor;
  private String port;
  private List<FileConfig> archivos;
}
