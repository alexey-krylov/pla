package com.pla.grouphealth.claim.cashless.repository;

import com.pla.grouphealth.claim.cashless.domain.model.PreAuthorizationId;
import com.pla.grouphealth.claim.cashless.domain.model.PreAuthorizationRequestId;
import com.pla.sharedkernel.domain.model.ClaimSettlementId;
import org.bson.types.ObjectId;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

/**
 * Created by Mohan Sharma on 1/21/2016.
 */
public class PreAuthorizationIdToObjectIdConverter implements Converter<PreAuthorizationRequestId, ObjectId>{
    @Override
    public ObjectId convert(PreAuthorizationRequestId preAuthorizationRequestId) {
        return StringUtils.hasText(preAuthorizationRequestId.getPreAuthorizationRequestId()) ? new ObjectId(preAuthorizationRequestId.getPreAuthorizationRequestId()) : null;
    }
}
