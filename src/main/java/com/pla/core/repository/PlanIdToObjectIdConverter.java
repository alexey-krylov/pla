package com.pla.core.repository;

import com.pla.sharedkernel.identifier.PlanId;
import org.bson.types.ObjectId;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

/**
 * Created by pradyumna on 10-04-2015.
 */
public class PlanIdToObjectIdConverter implements Converter<PlanId, ObjectId> {

    @Override
    public ObjectId convert(PlanId source) {
        return StringUtils.hasText(source.getPlanId()) ? new ObjectId(source.getPlanId()) : null;
    }
}
