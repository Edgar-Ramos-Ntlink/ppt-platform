package com.business.unknow.model.dto.services;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.util.Date;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Builder
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationDto implements Serializable {

  private int id;
  private String event;
  private String emails;
  private String resume;
  private String notification;
  private Boolean active;
  private String updatedBy;
  private Date fechaCreacion;
  private Date fechaActualizacion;
}
