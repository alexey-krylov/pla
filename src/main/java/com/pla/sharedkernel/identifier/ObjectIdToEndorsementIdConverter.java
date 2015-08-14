package com.pla.sharedkernel.identifier;

import org.bson.types.ObjectId;
import org.springframework.core.convert.converter.Converter;

/**
 * Created by Samir on 8/4/2015.
 */
public class ObjectIdToEndorsementIdConverter implements Converter<ObjectId, EndorsementId> {
    @Override
    public EndorsementId convert(ObjectId source) {
        return source == null ? null : new EndorsementId(source.toString());
    }
}
