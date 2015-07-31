package com.pla.sharedkernel.identifier;

import org.bson.types.ObjectId;
import org.springframework.core.convert.converter.Converter;

/**
 * Created by Nthdimenzion on 30-Jul-15.
 */
public class ObjectIdToPolicyIdConverter implements Converter<ObjectId, PolicyId> {
    @Override
    public PolicyId convert(ObjectId id) {
        return id == null ? null : new PolicyId(id.toString());
    }
}
