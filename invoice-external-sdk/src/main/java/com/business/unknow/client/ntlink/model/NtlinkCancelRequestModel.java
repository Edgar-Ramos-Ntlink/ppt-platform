package com.business.unknow.client.ntlink.model;

public class NtlinkCancelRequestModel {

  private String user;
  private String userPass;
  private String uuid;
  private String motivo;
  private String folioSustituto;
  private String rfcEmisor;
  private String expresion;
  private String rfcReceptor;

  public NtlinkCancelRequestModel(
      String user,
      String userPass,
      String uuid,
      String folioSustituto,
      String motivo,
      String rfcEmisor,
      String rfcReceptor,
      String expresion) {
    super();
    this.user = user;
    this.userPass = userPass;
    this.uuid = uuid;
    this.motivo = motivo;
    this.folioSustituto=folioSustituto;
    this.rfcEmisor = rfcEmisor;
    this.rfcReceptor = rfcReceptor;
    this.expresion = expresion;
  }

  public NtlinkCancelRequestModel() {
    super();
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public String getUserPass() {
    return userPass;
  }

  public void setUserPass(String userPass) {
    this.userPass = userPass;
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getRfcEmisor() {
    return rfcEmisor;
  }

  public void setRfcEmisor(String rfcEmisor) {
    this.rfcEmisor = rfcEmisor;
  }

  public String getRfcReceptor() {
    return rfcReceptor;
  }

  public void setRfcReceptor(String rfcReceptor) {
    this.rfcReceptor = rfcReceptor;
  }

  public String getExpresion() {
    return expresion;
  }

  public void setExpresion(String expresion) {
    this.expresion = expresion;
  }

  public String getMotivo() {
    return motivo;
  }

  public void setMotivo(String motivo) {
    this.motivo = motivo;
  }

  public String getFolioSustituto() {
    return folioSustituto;
  }

  public void setFolioSustituto(String folioSustituto) {
    this.folioSustituto = folioSustituto;
  }

  @Override
  public String toString() {
    return "NtlinkCancelRequestModel{" +
            "user='" + user + '\'' +
            ", userPass='" + userPass + '\'' +
            ", uuid='" + uuid + '\'' +
            ", motivo='" + motivo + '\'' +
            ", folioSustituto='" + folioSustituto + '\'' +
            ", rfcEmisor='" + rfcEmisor + '\'' +
            ", expresion='" + expresion + '\'' +
            ", rfcReceptor='" + rfcReceptor + '\'' +
            '}';
  }
}
