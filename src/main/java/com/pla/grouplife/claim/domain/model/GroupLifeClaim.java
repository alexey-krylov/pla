package com.pla.grouplife.claim.domain.model;

import com.pla.grouplife.sharedresource.model.vo.CoveragePremiumDetail;
import com.pla.grouplife.sharedresource.model.vo.PlanPremiumDetail;
import com.pla.grouplife.sharedresource.model.vo.Proposer;
import com.pla.sharedkernel.domain.model.ClaimId;
import com.pla.sharedkernel.domain.model.ClaimNumber;
import com.pla.sharedkernel.domain.model.ClaimType;
import com.pla.sharedkernel.domain.model.Policy;
import lombok.*;
import org.axonframework.domain.AbstractAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

/**
 * Created by Mirror on 8/19/2015.
 */
@Document(collection = "group_life_claim")
@Getter
@Setter(value = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode(of = "claimId")
public class GroupLifeClaim  extends AbstractAggregateRoot<ClaimId> {

    @Id
    @AggregateIdentifier
    private ClaimId claimId;

    private ClaimNumber claimNumber;

    private ClaimType claimType;

    private Policy policy;

    private Proposer proposer;

    private AssuredDetail assuredDetail;

    private PlanPremiumDetail planPremiumDetail;

    private Set<CoveragePremiumDetail> coveragePremiumDetails;

    private  BankDetails bankDetails;

    private DateTime intimationDate;

    private ClaimStatus claimStatus;


    @Override
    public ClaimId getIdentifier() {
        return this.claimId;
    }
}
