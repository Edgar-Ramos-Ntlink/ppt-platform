package com.business.unknow.model.dto.files;

import static com.business.unknow.Constants.JSON_DATETIME_FORMAT;

import com.business.unknow.enums.TipoArchivo;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FacturaFileDto implements Serializable {

  private static final long serialVersionUID = -5350228749080896941L;
  private Integer id;
  private String tipoArchivo;
  private TipoArchivo fileFormat;
  private String folio;
  private String data;
  private ByteArrayOutputStream outputStream;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = JSON_DATETIME_FORMAT)
  private Date fechaCreacion;
}
