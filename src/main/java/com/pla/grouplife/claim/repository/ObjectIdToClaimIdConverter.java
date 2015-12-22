package com.pla.grouplife.claim.repository;

import com.pla.sharedkernel.domain.model.ClaimId;
import org.bson.types.ObjectId;
import org.springframework.core.convert.converter.Converter;

/**
 * Created by ak on 12/11/2015.
 */
public class ObjectIdToClaimIdConverter implements Converter<ObjectId, ClaimId> {

    @Override
    public ClaimId convert(ObjectId id)
    {
        return id == null ? null : new ClaimId(id.toString());
    }
}