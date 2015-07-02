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
    private String employeeId;
    private String companyNameAndPostalAddress;
    private String basicSalary;
    private SalaryPer salaryPer;
    private String bankBranchSortCode;
    private String bankName;
    private String bankBranchName;
    private String bankAccountNumber;
    private DateTime proposalSignDate;
    private PremiumPaymentMethod premiumPaymentMethod;
    private String manNumber;
    private String BankAccountType;

}
