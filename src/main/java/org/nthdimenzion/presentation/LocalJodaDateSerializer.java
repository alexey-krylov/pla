package org.nthdimenzion.presentation;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

import java.io.IOException;

/**
 * Created by Samir on 4/13/2015.
 */
public class LocalJodaDateSerializer extends JsonSerializer<LocalDate>{
    @Override
    public void serialize(LocalDate value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        value.toString(DateTimeFormat.forPattern("dd/MM/yyyy"));
    }
}
