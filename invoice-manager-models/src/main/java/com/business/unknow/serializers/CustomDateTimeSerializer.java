package com.business.unknow.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.business.unknow.Constants.JSON_DATE_FORMAT;

public class CustomDateTimeSerializer extends JsonSerializer<Date> {

    static final SimpleDateFormat dateTimeSdf = new SimpleDateFormat(JSON_DATE_FORMAT);

    @Override
    public void serialize(Date value, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
            String s = dateTimeSdf.format(value);
            gen.writeString(s);
    }
}
