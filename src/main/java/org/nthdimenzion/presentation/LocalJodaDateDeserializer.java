package org.nthdimenzion.presentation;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

import java.io.IOException;

/**
 * Created by Samir on 4/9/2015.
 */
public class LocalJodaDateDeserializer extends JsonDeserializer<LocalDate> {


    @Override
    public LocalDate deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String dateString = jp.getText().trim();
        return LocalDate.parse(dateString, DateTimeFormat.forPattern("dd/MM/yyyy"));
    }
}
