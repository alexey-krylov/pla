package com.pla.quotation.repository;

import com.pla.sharedkernel.identifier.QuotationId;
import org.bson.types.ObjectId;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

/**
 * Created by Samir on 4/15/2015.
 */
public class QuotationIdToObjectIdConverter implements Converter<QuotationId, ObjectId> {
    @Override
    public ObjectId convert(QuotationId source) {
        return StringUtils.hasText(source.getQuotationId()) ? new ObjectId(source.getQuotationId()) : null;
    }
}
