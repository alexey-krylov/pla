package com.pla.grouplife.claim.domain.model;

import lombok.Getter;

/**
 * Created by Mirror on 8/19/2015.
 */
@Getter
public class BankDetails {

    private String bankName;

    private String bankBranchName;

    private String bankAccountType;

    private String bankAccountNumber;

    public  BankDetails(String bankName, String bankBranchName, String bankAccountType, String bankAccountNumber)
    {
       this.bankName=bankName;
        this.bankBranchName=bankBranchName;
        this.bankAccountType=bankAccountType;
        this.bankAccountNumber=bankAccountNumber;
    }
}
