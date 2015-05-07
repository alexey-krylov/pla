package com.pla.grouphealth.repository;

import com.pla.sharedkernel.identifier.QuotationId;
import org.bson.types.ObjectId;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

/**
 * Created by Karunakar on 4/30/2015.
 */
public class QuotationIdToObjectIdConverter implements Converter<QuotationId, ObjectId> {
    @Override
    public ObjectId convert(QuotationId source) {
        return StringUtils.hasText(source.getQuotationId()) ? new ObjectId(source.getQuotationId()) : null;
    }
}
