package com.pla.grouphealth.claim.cashless.repository;

import com.pla.grouphealth.claim.cashless.domain.model.PreAuthorizationRequestId;
import org.bson.types.ObjectId;
import org.springframework.core.convert.converter.Converter;

/**
 * Created by Mohan Sharma on 1/21/2016.
 */
public class ObjectIdToPreAuthorizationIdConverter implements Converter<ObjectId,PreAuthorizationRequestId>{
    @Override
    public PreAuthorizationRequestId convert(ObjectId objectId) {
        return objectId == null ? null : new PreAuthorizationRequestId(objectId.toString());
    }
}
