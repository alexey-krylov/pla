package com.pla.grouphealth.repository;

import com.pla.sharedkernel.identifier.QuotationId;
import org.bson.types.ObjectId;
import org.springframework.core.convert.converter.Converter;

/**
 * Created by Karunakar on 4/30/2015.
 */
public class ObjectIdToQuotationIdConverter implements Converter<ObjectId, QuotationId> {
    @Override
    public QuotationId convert(ObjectId id) {
        return id == null ? null : new QuotationId(id.toString());
    }
}
