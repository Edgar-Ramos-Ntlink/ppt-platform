package com.business.unknow.model.dto.cfdi;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RelacionadoDto implements Serializable {

  private static final long serialVersionUID = 6588638217033669199L;
  private int id;
  private String tipoRelacion;
  private String relacion;
}
