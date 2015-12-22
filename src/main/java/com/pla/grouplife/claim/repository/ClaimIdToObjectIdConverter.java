package com.pla.grouplife.claim.repository;

import com.pla.sharedkernel.domain.model.ClaimId;
import org.bson.types.ObjectId;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

/**
 * Created by ak
 */
public class ClaimIdToObjectIdConverter  implements Converter<ClaimId, ObjectId> {
    @Override
    public ObjectId convert(ClaimId source) {
        return StringUtils.hasText(source.getClaimId()) ? new ObjectId(source.getClaimId()) : null;
    }
}
