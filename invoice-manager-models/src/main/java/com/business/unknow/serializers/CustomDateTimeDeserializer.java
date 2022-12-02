package com.business.unknow.serializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Date;

public class CustomDateTimeDeserializer extends JsonDeserializer<Date> {
  @Override
  public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
      throws IOException {
    try {
      LocalDateTime localDateTime = LocalDateTime.parse(jsonParser.getText());
      Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
      return Date.from(instant);
    } catch (DateTimeParseException e) {
      return null;
    }
  }
}
