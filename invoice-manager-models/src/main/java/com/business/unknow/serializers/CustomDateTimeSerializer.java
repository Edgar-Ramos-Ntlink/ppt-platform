package com.business.unknow.serializers;

import static com.business.unknow.Constants.JSON_DATE_FORMAT;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class CustomDateTimeSerializer extends JsonSerializer<Date> {

  static final SimpleDateFormat sdf = new SimpleDateFormat(JSON_DATE_FORMAT);

  @Override
  public void serialize(Date value, JsonGenerator gen, SerializerProvider serializerProvider)
      throws IOException {
    sdf.setTimeZone(TimeZone.getTimeZone("America/Mexico_City"));
    String s = sdf.format(value);
    gen.writeString(s);
  }
}
