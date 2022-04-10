package com.business.unknow.model.rest;

import lombok.Getter;
import lombok.Setter;

/** @author eej000f */
@Getter
@Setter
public class MessageResponse {
  private String name;

  public MessageResponse() {}

  public MessageResponse(String name) {
    super();
    this.name = name;
  }
}
