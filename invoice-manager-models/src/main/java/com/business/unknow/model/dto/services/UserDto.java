package com.business.unknow.model.dto.services;

import static com.business.unknow.Constants.JSON_DATETIME_FORMAT;

import com.business.unknow.model.menu.MenuItem;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
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
public class UserDto implements Serializable {

  private static final long serialVersionUID = -4269713581531174125L;

  private Integer id;

  private String email;

  private boolean activo;

  private String name;

  private String alias;

  private String urlPicture;

  private List<RoleDto> roles;

  private List<MenuItem> menu;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = JSON_DATETIME_FORMAT)
  private Date fechaCreacion;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = JSON_DATETIME_FORMAT)
  private Date fechaActualizacion;
}
