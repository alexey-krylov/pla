package com.pla.grouphealth.claim.cashless.domain.model;

import com.pla.core.hcp.domain.model.HCPCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

/**
 * Created by Mohan Sharma on 12/30/2015.
 */
@Document(collection = "PRE_AUTHORIZATION_DETAIL")
@NoArgsConstructor
@Getter
public class PreAuthorization {
    @Id
    private PreAuthorizationId preAuthorizationId;
    private HCPCode hcpCode;
    private int batchNumber;
    private DateTime batchDate;
    private Set<PreAuthorizationDetail> preAuthorizationDetails;

    public PreAuthorization updateWithPreAuthorizationDetail(Set<PreAuthorizationDetail> preAuthorizationDetails) {
        this.preAuthorizationDetails = preAuthorizationDetails;
        return this;
    }

    public PreAuthorization updateWithHcpCode(HCPCode hcpCode) {
        this.hcpCode = hcpCode;
        return this;
    }

    public PreAuthorization updateWithBatchDate(DateTime batchDate) {
        this.batchDate = batchDate;
        return this;
    }

    public PreAuthorization updateWithBatchNumber(int batchNumber) {
        this.batchNumber = batchNumber;
        return this;
    }

    public PreAuthorization updateWithPreAuthorizationId(PreAuthorizationId preAuthorizationId) {
        this.preAuthorizationId = preAuthorizationId;
        return this;
    }
}
