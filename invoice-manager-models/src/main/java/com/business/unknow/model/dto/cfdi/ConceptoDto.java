package com.business.unknow.model.dto.cfdi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@Getter
@Setter
@ToString
public class ConceptoDto implements Serializable {

  private static final long serialVersionUID = 1690079459401358817L;
  private Integer id;
  private String claveProdServ;
  private String descripcionCUPS;
  private String noIdentificacion;
  private BigDecimal cantidad;
  private String claveUnidad;
  private String unidad;
  private String descripcion;
  private BigDecimal valorUnitario;
  private BigDecimal importe;
  private BigDecimal descuento;
  private List<ImpuestoDto> impuestos = new ArrayList<>();
  private List<RetencionDto> retenciones = new ArrayList<>();
}
