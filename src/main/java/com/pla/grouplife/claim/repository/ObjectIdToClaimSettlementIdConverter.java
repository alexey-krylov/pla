package com.pla.grouplife.claim.repository;

import com.pla.sharedkernel.domain.model.ClaimSettlementId;
import org.bson.types.ObjectId;
import org.springframework.core.convert.converter.Converter;
/**
 * Created by ak on 21/12/2015.
 */
public class ObjectIdToClaimSettlementIdConverter implements Converter<ObjectId,ClaimSettlementId>
         {
             @Override
             public ClaimSettlementId convert(ObjectId id)
             {
                 return id == null ? null : new ClaimSettlementId(id.toString());
             }
}


