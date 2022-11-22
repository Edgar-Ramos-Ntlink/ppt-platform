package com.business.unknow.model.dto.files;

import static com.business.unknow.Constants.JSON_DAY_FORMAT;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * ResourceFileDto is a class created to saves metadata information related to S3 bucket files,
 * where tipoArchivo stores the file type(ex: CERT,KEY,LOGO,IMAGEN), tipoRecurso describe the origin
 * of the resource (ex: EMPRESA,PAGO, FACTURA, DOC, etc), referencia links the resource with an
 * unique identifier of other tables(ex: 3_AME1RASE20200420093307PUE, AME140512D80)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@ToString
public class ResourceFileDto implements Serializable {

  private static final long serialVersionUID = -8750055024664848580L;
  private Integer id;
  private String tipoArchivo;
  private String referencia;
  private String nombre;
  private String tipoRecurso;
  private String formato;
  private String extension;
  private String data;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = JSON_DAY_FORMAT)
  private Date fechaCreacion;

  public ResourceFileDto() {}

  public ResourceFileDto(
      String tipoArchivo, String referencia, String tipoRecurso, String data, String formato) {
    this.tipoArchivo = tipoArchivo;
    this.referencia = referencia;
    this.tipoRecurso = tipoRecurso;
    this.formato = formato;
    this.data = data;
  }
}
