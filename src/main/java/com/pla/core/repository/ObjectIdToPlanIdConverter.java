package com.pla.core.repository;

import com.pla.sharedkernel.identifier.PlanId;
import org.bson.types.ObjectId;
import org.springframework.core.convert.converter.Converter;

/**
 * Created by pradyumna on 10-04-2015.
 */
public class ObjectIdToPlanIdConverter implements Converter<ObjectId, PlanId> {

    @Override
    public PlanId convert(ObjectId id) {
        return id == null ? null : new PlanId(id.toString());
    }
}
