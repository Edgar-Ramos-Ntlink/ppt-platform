package com.business.unknow.model.menu;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
@ToString
public class MenuItem implements Serializable {

  private static final long serialVersionUID = 1656844506912422028L;
  private String title;
  private String icon;
  private String link;
  private Map<String, String> queryParams;
  private Boolean group;
  private List<MenuItem> children;
  private Boolean home;
}
