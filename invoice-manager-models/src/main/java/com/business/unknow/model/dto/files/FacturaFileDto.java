/** */
package com.business.unknow.model.dto.files;

import com.business.unknow.Constants;
import com.business.unknow.enums.TipoArchivoEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.Date;

/** @author ralfdemoledor */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FacturaFileDto implements Serializable {

  private static final long serialVersionUID = -5350228749080896941L;
  private Integer id;
  private String tipoArchivo;
  private TipoArchivoEnum fileFormat;
  private String folio;
  private String data;
  private ByteArrayOutputStream outputStream;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.JSON_DATETIME_FORMAT)
  private Date fechaCreacion;

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

  public String getFolio() {
    return folio;
  }

  public void setFileFormat(TipoArchivoEnum fileFormat) {
    this.fileFormat = fileFormat;
  }

  public TipoArchivoEnum getFileFormat() {
    return fileFormat;
  }

  public void setFolio(String folio) {
    this.folio = folio;
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

  public void setOutputStream(ByteArrayOutputStream outputStream) {
    this.outputStream = outputStream;
  }

  public ByteArrayOutputStream getOutputStream() {
    return outputStream;
  }

  @Override
  public String toString() {
    return "FacturaFileDto [id="
        + id
        + ", tipoArchivo="
        + tipoArchivo
        + ", folio="
        + folio
        + ", fechaCreacion="
        + fechaCreacion
        + "]";
  }
}
