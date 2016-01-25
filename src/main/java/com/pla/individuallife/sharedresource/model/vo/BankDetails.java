package com.pla.individuallife.sharedresource.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Karunakar on 7/6/2015.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BankDetails {
    private String bankBranchSortCode;
    private String bankName;
    private String bankBranchName;
    private String bankAccountNumber;
    private String bankAccountType;

}
