package com.pla.core.repository;

import org.joda.time.LocalDate;
import org.springframework.core.convert.converter.Converter;

/**
 * Created by pradyumna on 14-04-2015.
 */
public class StringToLocalDateConverter implements Converter<String, LocalDate> {
    @Override
    public LocalDate convert(String source) {
        return LocalDate.parse(source);
    }
}
