package com.business.unknow.enums;

public enum TipoEmail {
  SEMEL_JACK("", "587"),
  GMAIL("smtp.gmail.com", "587");

  private String host;
  private String port;

  TipoEmail(String host, String port) {
    this.host = host;
    this.port = port;
  }

  public String getHost() {
    return host;
  }

  public String getPort() {
    return port;
  }

}
