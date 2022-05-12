package com.business.unknow.model.config;

import com.business.unknow.enums.TipoArchivo;
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
public class FileConfig {

  private TipoArchivo tipoArchivo;
  private String nombre;
  private String base64Content;
}
