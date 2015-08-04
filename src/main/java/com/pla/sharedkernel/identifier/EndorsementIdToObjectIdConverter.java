package com.pla.sharedkernel.identifier;

import org.bson.types.ObjectId;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

/**
 * Created by Samir on 8/4/2015.
 */
public class EndorsementIdToObjectIdConverter implements Converter<EndorsementId, ObjectId> {

    @Override
    public ObjectId convert(EndorsementId source) {
        return StringUtils.hasText(source.getEndorsementId()) ? new ObjectId(source.getEndorsementId()) : null;
    }
}
