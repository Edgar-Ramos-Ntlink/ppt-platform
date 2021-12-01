package com.business.unknow.model.dto.files;

import com.business.unknow.Constants;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.util.Date;

/**
 * @author ralfdemoledor
 *     <p>ResourceFileDto is a class created to saves metadata information related to S3 bucket
 *     files, where tipoArchivo stores the file type(ex: CERT,KEY,LOGO,IMAGEN), tipoRecurso describe
 *     the origin of the resource (ex: EMPRESA,PAGO, FACTURA, DOC, etc), referencia links the
 *     resource with an unique identifier of other tables(ex: 3_AME1RASE20200420093307PUE,
 *     AME140512D80)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResourceFileDto implements Serializable {

  private static final long serialVersionUID = -8750055024664848580L;
  private Integer id;
  private String tipoArchivo;
  private String referencia;
  private String nombre;
  private String tipoRecurso;
  private String formato;
  private String extension;
  @Deprecated // dont use data, this field will be removed once S3 refactor will be completed
  private String data;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.JSON_DATETIME_FORMAT)
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

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getTipoArchivo() {
    return tipoArchivo;
  }

  public void setTipoArchivo(String tipoArchivo) {
    this.tipoArchivo = tipoArchivo;
  }

  public String getReferencia() {
    return referencia;
  }

  public void setReferencia(String referencia) {
    this.referencia = referencia;
  }

  public String getTipoRecurso() {
    return tipoRecurso;
  }

  public void setTipoRecurso(String tipoRecurso) {
    this.tipoRecurso = tipoRecurso;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }

  public Date getFechaCreacion() {
    return fechaCreacion;
  }

  public void setFechaCreacion(Date fechaCreacion) {
    this.fechaCreacion = fechaCreacion;
  }

  public String getFormato() {
    return formato;
  }

  public void setFormato(String formato) {
    this.formato = formato;
  }

  public String getExtension() {
    return extension;
  }

  public void setExtension(String extension) {
    this.extension = extension;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  @Override
  public String toString() {
    return "ResourceFileDto{" +
            "id=" + id +
            ", tipoArchivo='" + tipoArchivo + '\'' +
            ", referencia='" + referencia + '\'' +
            ", nombre='" + nombre + '\'' +
            ", tipoRecurso='" + tipoRecurso + '\'' +
            ", formato='" + formato + '\'' +
            ", extension='" + extension + '\'' +
            ", fechaCreacion=" + fechaCreacion +
            '}';
  }
}
