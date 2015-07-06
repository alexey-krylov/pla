package com.pla.individuallife.proposal.domain.model;

import com.pla.publishedlanguage.domain.model.PremiumFrequency;
import lombok.Getter;
import lombok.ToString;
import org.joda.time.DateTime;

/**
 * Created by Karunakar on 7/2/2015.
 */
@Getter
@ToString
public class PremiumPaymentDetails {
    private PremiumFrequency premiumFrequency;
    private PremiumPaymentMethod premiumPaymentMethod;
    private EmployerDetails employerDetails;
    private BankDetails bankDetails;
    private DateTime proposalSignDate;
}
