package com.business.unknow.model.dto.catalogs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Builder
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClaveUnidadDto implements Serializable {

  private static final long serialVersionUID = 2485025222885106558L;

  private String clave;
  private String tipo;
  private String descripcion;
  private String nombre;
}
