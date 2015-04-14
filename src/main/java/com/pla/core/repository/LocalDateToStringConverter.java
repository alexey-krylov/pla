package com.pla.core.repository;

import org.joda.time.LocalDate;
import org.springframework.core.convert.converter.Converter;

/**
 * Created by pradyumna on 14-04-2015.
 */
public class LocalDateToStringConverter implements Converter<LocalDate, String> {
    @Override
    public String convert(LocalDate localDate) {
        return localDate.toString();
    }
}
