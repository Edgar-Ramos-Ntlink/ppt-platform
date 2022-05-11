package com.business.unknow.model.config;

import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@ToString
public class MailContent {

  private String subject;
  private String bodyText;
  private Map<String, MailFile> attachments;

  @Builder
  @Getter
  @Setter
  @ToString
  public static class MailFile {
    private String data;
    private String type;
  }
}
