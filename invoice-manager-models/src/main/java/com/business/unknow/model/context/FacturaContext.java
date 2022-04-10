package com.business.unknow.model.context;

import com.business.unknow.model.cfdi.Cfdi;
import com.business.unknow.model.dto.FacturaDto;
import com.business.unknow.model.dto.files.FacturaFileDto;
import com.business.unknow.model.dto.pagos.PagoDto;
import com.business.unknow.model.dto.services.ContribuyenteDto;
import com.business.unknow.model.dto.services.EmpresaDto;
import java.io.Serializable;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Setter
@Getter
public class FacturaContext implements Serializable {

  private static final long serialVersionUID = 661221684069242273L;
  private String tipoFactura;
  private String tipoDocumento;
  private FacturaDto facturaDto;
  private FacturaDto facturaPadreDto;
  private List<FacturaDto> complementos;
  private List<PagoDto> pagos;
  private PagoDto pagoCredito;
  private PagoDto currentPago;
  private boolean valid;
  private String ruleErrorDesc;
  private String suiteError;
  private EmpresaDto empresaDto;
  private ContribuyenteDto contribuyenteDto;
  private Cfdi cfdi;
  private List<FacturaFileDto> facturaFilesDto;
  private String xml;
  private int ctdadComplementos;
  private int idPago;

  public FacturaContext() {
    valid = true;
  }
}
