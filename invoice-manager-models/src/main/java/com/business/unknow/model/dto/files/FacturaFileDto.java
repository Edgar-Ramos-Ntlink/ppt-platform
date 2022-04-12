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
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/** @author ralfdemoledor */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@ToString
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
}
