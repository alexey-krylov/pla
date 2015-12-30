package com.pla.grouphealth.claim.cashless.domain.model;

import com.pla.core.hcp.domain.model.HCPCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by Mohan Sharma on 12/30/2015.
 */
@Document(collection = "PRE_AUTHORIZATION_DETAIL")
@NoArgsConstructor
@Getter
public class PreAuthorizationDetail {
    @Id
    private PreAuthorizationDetailId preAuthorizationDetailId;
    private HCPCode hcpCode;
    private int batchNumber;
    private DateTime batchDate;
}
