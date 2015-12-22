package com.pla.grouplife.claim.repository;

import com.pla.sharedkernel.domain.model.ClaimSettlementId;
import org.bson.types.ObjectId;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;
/**
 * Created by ak on 21/12/2015.
 */
public class ClaimSettlementIdToObjectIdConverter  implements Converter<ClaimSettlementId,ObjectId> {

    @Override
    public ObjectId convert(ClaimSettlementId source) {
        return StringUtils.hasText(source.getClaimSettlementId()) ? new ObjectId(source.getClaimSettlementId()) : null;
    }
}



