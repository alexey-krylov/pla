package com.pla.sharedkernel.identifier;

import org.bson.types.ObjectId;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

/**
 * Created by Nthdimenzion on 30-Jul-15.
 */
public class PolicyIdToObjectIdConverter implements Converter<PolicyId, ObjectId> {
    @Override
    public ObjectId convert(PolicyId source) {
        return StringUtils.hasText(source.getPolicyId()) ? new ObjectId(source.getPolicyId()) : null;
    }
}
