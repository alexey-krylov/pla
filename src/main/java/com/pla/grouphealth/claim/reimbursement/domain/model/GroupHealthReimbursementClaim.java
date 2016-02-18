package com.pla.grouphealth.claim.reimbursement.domain.model;

/**
 * Author - Mohan Sharma Created on 12/30/2015.
 */

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.axonframework.domain.AbstractAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "GROUP_HEALTH_REIMBURSEMENT_CLAIM")
@NoArgsConstructor
@Getter
public class GroupHealthReimbursementClaim  extends AbstractAggregateRoot<String>{
    @AggregateIdentifier
    @Id
    private String groupHealthReimbursementClaimId;
    @Override
    public String getIdentifier() {
        return groupHealthReimbursementClaimId;
    }
}
