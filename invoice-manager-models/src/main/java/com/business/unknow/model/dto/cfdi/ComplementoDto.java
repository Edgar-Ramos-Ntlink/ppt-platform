package com.business.unknow.model.dto.cfdi;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class ComplementoDto {

  private List<CfdiPagoDto> pagos;

  public ComplementoDto() {
    this.pagos = new ArrayList<CfdiPagoDto>();
  }
}
