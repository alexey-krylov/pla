package com.pla.individuallife.repository;

import com.pla.sharedkernel.identifier.QuotationId;
import org.bson.types.ObjectId;
import org.springframework.core.convert.converter.Converter;

/**
 * Created by Karunakar on 5/13/2015.
 */
public class ObjectIdToQuotationIdConverter implements Converter<ObjectId, QuotationId> {
    @Override
    public QuotationId convert(ObjectId id) {
        return id == null ? null : new QuotationId(id.toString());
    }
}
