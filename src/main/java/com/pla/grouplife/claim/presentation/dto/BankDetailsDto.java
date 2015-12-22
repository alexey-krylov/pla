package com.pla.grouplife.claim.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by nthdimensioncompany on 28/10/2015.
 */
@Getter
@Setter
@NoArgsConstructor

public class BankDetailsDto {
    private String bankName;

    private String bankBranchName;

    private String bankAccountType;

    private String bankAccountNumber;

    public  BankDetailsDto(String bankName, String bankBranchName, String bankAccountType, String bankAccountNumber)
    {
        this.bankName=bankName;
        this.bankBranchName=bankBranchName;
        this.bankAccountType=bankAccountType;
        this.bankAccountNumber=bankAccountNumber;
    }
}
