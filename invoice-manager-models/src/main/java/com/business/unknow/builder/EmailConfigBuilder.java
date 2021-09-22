package com.business.unknow.builder;

import com.business.unknow.model.config.EmailConfig;
import com.business.unknow.model.config.FileConfig;
import java.util.ArrayList;
import java.util.List;

public class EmailConfigBuilder extends AbstractBuilder<EmailConfig> {

  public EmailConfigBuilder() {
    super(new EmailConfig());
  }

  public EmailConfigBuilder setAsunto(String asunto) {
    instance.setAsunto(asunto);
    return this;
  }

  public EmailConfigBuilder setCuerpo(String cuerpo) {
    instance.setCuerpo(cuerpo);
    return this;
  }

  public EmailConfigBuilder setEmisor(String emisor) {
    instance.setEmisor(emisor);
    return this;
  }

  public EmailConfigBuilder setPwEmisor(String pwEmisor) {
    instance.setPwEmisor(pwEmisor);
    return this;
  }

  public EmailConfigBuilder setReceptor(List<String> receptor) {
    instance.setReceptor(receptor);
    return this;
  }

  public EmailConfigBuilder setDominio(String dominio) {
    instance.setDominio(dominio);
    return this;
  }

  public EmailConfigBuilder addReceptor(String receptor) {
    if (receptor != null && !receptor.isEmpty() && !receptor.contains("Sin asignar")) {
      if (instance.getReceptor() == null) {
        instance.setReceptor(new ArrayList<>());
      }
      instance.getReceptor().add(receptor);
    }
    return this;
  }

  public EmailConfigBuilder addReceptors(String receptors) {
    for (String receptor : receptors.split(",")) {
      addReceptor(receptor);
    }
    return this;
  }

  public EmailConfigBuilder addArchivo(FileConfig fileConfig) {
    if (instance.getArchivos() == null) {
      instance.setArchivos(new ArrayList<>());
    }
    instance.getArchivos().add(fileConfig);
    return this;
  }

  public EmailConfigBuilder setArchivos(List<FileConfig> archivos) {
    instance.setArchivos(archivos);
    return this;
  }

  public EmailConfigBuilder setPort(String port) {
    instance.setPort(port);
    return this;
  }
}
