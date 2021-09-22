package com.business.unknow.model.context;

import com.business.unknow.model.cfdi.Cfdi;
import com.business.unknow.model.dto.FacturaDto;
import com.business.unknow.model.dto.files.FacturaFileDto;
import com.business.unknow.model.dto.pagos.PagoDto;
import com.business.unknow.model.dto.services.ContribuyenteDto;
import com.business.unknow.model.dto.services.EmpresaDto;
import java.io.Serializable;
import java.util.List;

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

  public String getTipoFactura() {
    return tipoFactura;
  }

  public void setTipoFactura(String tipoFactura) {
    this.tipoFactura = tipoFactura;
  }

  public FacturaDto getFacturaDto() {
    return facturaDto;
  }

  public void setFacturaDto(FacturaDto facturaDto) {
    this.facturaDto = facturaDto;
  }

  public boolean isValid() {
    return valid;
  }

  public void setValid(boolean valid) {
    this.valid = valid;
  }

  public List<FacturaDto> getComplementos() {
    return complementos;
  }

  public void setComplementos(List<FacturaDto> complementos) {
    this.complementos = complementos;
  }

  public String getRuleErrorDesc() {
    return ruleErrorDesc;
  }

  public void setRuleErrorDesc(String ruleErrorDesc) {
    this.ruleErrorDesc = ruleErrorDesc;
  }

  public String getSuiteError() {
    return suiteError;
  }

  public void setSuiteError(String suiteError) {
    this.suiteError = suiteError;
  }

  public FacturaDto getFacturaPadreDto() {
    return facturaPadreDto;
  }

  public void setFacturaPadreDto(FacturaDto facturaPadreDto) {
    this.facturaPadreDto = facturaPadreDto;
  }

  public List<PagoDto> getPagos() {
    return pagos;
  }

  public void setPagos(List<PagoDto> pagos) {
    this.pagos = pagos;
  }

  public EmpresaDto getEmpresaDto() {
    return empresaDto;
  }

  public void setEmpresaDto(EmpresaDto empresaDto) {
    this.empresaDto = empresaDto;
  }

  public Cfdi getCfdi() {
    return cfdi;
  }

  public void setCfdi(Cfdi cfdi) {
    this.cfdi = cfdi;
  }

  public String getTipoDocumento() {
    return tipoDocumento;
  }

  public void setTipoDocumento(String tipoDocumento) {
    this.tipoDocumento = tipoDocumento;
  }

  public List<FacturaFileDto> getFacturaFilesDto() {
    return facturaFilesDto;
  }

  public void setFacturaFilesDto(List<FacturaFileDto> facturaFilesDto) {
    this.facturaFilesDto = facturaFilesDto;
  }

  public String getXml() {
    return xml;
  }

  public void setXml(String xml) {
    this.xml = xml;
  }

  public PagoDto getPagoCredito() {
    return pagoCredito;
  }

  public void setPagoCredito(PagoDto pagoCredito) {
    this.pagoCredito = pagoCredito;
  }

  public int getCtdadComplementos() {
    return ctdadComplementos;
  }

  public void setCtdadComplementos(int ctdadComplementos) {
    this.ctdadComplementos = ctdadComplementos;
  }

  public PagoDto getCurrentPago() {
    return currentPago;
  }

  public ContribuyenteDto getContribuyenteDto() {
    return contribuyenteDto;
  }

  public void setContribuyenteDto(ContribuyenteDto contribuyenteDto) {
    this.contribuyenteDto = contribuyenteDto;
  }

  public void setCurrentPago(PagoDto currentPago) {
    this.currentPago = currentPago;
  }

  public int getIdPago() {
    return idPago;
  }

  public void setIdPago(int idPago) {
    this.idPago = idPago;
  }

  @Override
  public String toString() {
    return "FacturaContext [tipoFactura="
        + tipoFactura
        + ", tipoDocumento="
        + tipoDocumento
        + ", facturaDto="
        + facturaDto
        + ", facturaPadreDto="
        + facturaPadreDto
        + ", complementos="
        + complementos
        + ", pagos="
        + pagos
        + ", pagoCredito="
        + pagoCredito
        + ", currentPago="
        + currentPago
        + ", valid="
        + valid
        + ", ruleErrorDesc="
        + ruleErrorDesc
        + ", suiteError="
        + suiteError
        + ", empresaDto="
        + empresaDto
        + ", contribuyenteDto="
        + contribuyenteDto
        + ", cfdi="
        + cfdi
        + ", facturaFilesDto="
        + facturaFilesDto
        + ", xml="
        + xml
        + ", ctdadComplementos="
        + ctdadComplementos
        + "]";
  }
}
