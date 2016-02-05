package com.pla.grouphealth.claim.cashless.presentation.dto.claim;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Rudra on 2/4/2016.
 */
@Getter
@Setter
@NoArgsConstructor
public class SearchGroupHealthCashlessClaimDto {

        private String batchNumber;
        private String policyNumber;
        private String policyHolderName;
        private String clientId;
        private String hcpCode;
        private String preAuthorizationId;
        private String underwriterLevel;

        public SearchGroupHealthCashlessClaimDto updateWithUnderwriterLevel(String level) {
            this.underwriterLevel = level;
            return this;
        }
    }


