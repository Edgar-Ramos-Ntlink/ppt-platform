/** */
package com.business.unknow.model.menu;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** @author ralfdemoledor */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MenuItem implements Serializable {

  private static final long serialVersionUID = 1656844506912422028L;
  private String title;
  private String icon;
  private String link;
  private Map<String, String> queryParams;
  private Boolean group;
  private List<MenuItem> children;
  private Boolean home;

  public MenuItem() {
    this.queryParams = new HashMap<>();
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getIcon() {
    return icon;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }

  public String getLink() {
    return link;
  }

  public void setLink(String link) {
    this.link = link;
  }

  public Boolean getGroup() {
    return group;
  }

  public void setGroup(Boolean group) {
    this.group = group;
  }

  public List<MenuItem> getChildren() {
    return children;
  }

  public void setChildren(List<MenuItem> children) {
    this.children = children;
  }

  public Boolean getHome() {
    return home;
  }

  public void setHome(Boolean home) {
    this.home = home;
  }

  public Map<String, String> getQueryParams() {
    return queryParams;
  }

  public void setQueryParams(Map<String, String> queryParams) {
    this.queryParams = queryParams;
  }

  @Override
  public String toString() {
    return "MenuItem [title="
        + title
        + ", icon="
        + icon
        + ", link="
        + link
        + ", queryParams="
        + queryParams
        + ", group="
        + group
        + ", children="
        + children
        + ", home="
        + home
        + "]";
  }
}
