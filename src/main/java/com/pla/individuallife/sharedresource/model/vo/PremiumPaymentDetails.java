package com.pla.individuallife.sharedresource.model.vo;

import com.pla.publishedlanguage.domain.model.PremiumFrequency;
import lombok.*;
import org.joda.time.DateTime;

import java.math.BigDecimal;

/**
 * Created by Karunakar on 7/2/2015.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PremiumPaymentDetails {
    private PremiumDetail premiumDetail;
    private PremiumFrequency premiumFrequency;
    private BigDecimal premiumFrequencyPayable;
    private PremiumPaymentMethod premiumPaymentMethod;
    private EmployerDetails employerDetails;
    private BankDetails bankDetails;
    private DateTime proposalSignDate;
}
