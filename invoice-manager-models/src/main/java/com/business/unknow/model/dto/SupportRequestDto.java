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
  private Integer clientId;
  private String clientEmail;
  private String companyRfc;
  private String companyName;
  private String contactPhone;
  private String contactEmail;
  private String contactName;
  private String product;
  private String status;
  private String supportType;
  private String agent;
  private String supportLevel;
  private String requestType;
  private String problem;
  private String solution;
  private String notes;
  private Date dueDate;
  private Date creation;
  private Date update;
}
