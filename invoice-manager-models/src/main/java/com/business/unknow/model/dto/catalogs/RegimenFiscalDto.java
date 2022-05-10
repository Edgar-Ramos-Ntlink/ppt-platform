package com.business.unknow.model.dto.catalogs;

import static com.business.unknow.Constants.JSON_DAY_FORMAT;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.util.Date;
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
public class RegimenFiscalDto implements Serializable {

  private static final long serialVersionUID = -6301363194780882965L;
  private Integer clave;
  private String descripcion;
  private boolean pFisica;
  private boolean pMoral;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = JSON_DAY_FORMAT)
  private Date inicioVigencia;
}
