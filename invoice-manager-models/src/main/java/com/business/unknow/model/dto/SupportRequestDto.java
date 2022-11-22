package com.business.unknow.model.dto;

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
public class SupportRequestDto implements Serializable {

  private Integer folio;
  private String contactEmail;
  private String contactName;
  private String contactPhone;
  private String module;
  private String status;
  private String agent;
  private String supportType;
  private String supportLevel;
  private String problem;
  private String errorMessage;
  private String solution;
  private String notes;
  private Date dueDate;
  private Date creation;
  private Date update;
}
